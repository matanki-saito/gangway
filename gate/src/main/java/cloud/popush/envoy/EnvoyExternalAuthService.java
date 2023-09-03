package cloud.popush.envoy;

import cloud.popush.util.ElasticsearchService;
import cloud.popush.util.NetUtils;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@GRpcService
public class EnvoyExternalAuthService extends AuthorizationGrpc.AuthorizationImplBase {

    private final List<GateFilter> gateFilterList;
    private final ElasticsearchService elasticsearchService;

    @Override
    public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
        var isPass = true;

        Map<String, Object> results = new HashMap<>();

        try {
            for (var filter : gateFilterList) {
                var result = filter.check(request);
                if (!result.isOk) {
                    isPass = false;
                }

                results.put(filter.getClass().getName(), result.reason);
            }
        } catch (Exception e) {
            isPass = false;
            log.error("System error:{},{}", e.getMessage(), e.getStackTrace());
        }

        logging(request, results, isPass);

        var response = CheckResponse.newBuilder()
                .setStatus(Status
                        .newBuilder()
                        .setCode(isPass ? Code.OK_VALUE : Code.PERMISSION_DENIED_VALUE)
                        .setMessage(isPass ? "OK" : "NG")
                        .build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private void logging(CheckRequest request, Map<String, Object> results, boolean isPass) {
        var host = NetUtils.getHost(request);
        results.put("pass", isPass);

        // TODO
        try {
            var query = NetUtils.getQuery(request);
            Map<String, String> contents = new HashMap<>();
            if (query.containsKey("plugin")) {
                contents.put("plugin", query.get("plugin"));
            }
            if (query.containsKey("cmd")) {
                contents.put("cmd", query.get("cmd"));
            }
            if (query.containsKey("page")) {
                contents.put("page", StringUtils.truncate(query.get("page"), 1500));
            }
            if (query.containsKey("msg")) {
                contents.put("msg", StringUtils.truncate(query.get("msg"), 1500));
            }
            if (query.containsKey("original")) {
                contents.put("original", StringUtils.truncate(query.get("original"), 1500));
            }

            results.put("content", contents);

        } catch (UnsupportedEncodingException e) {
            log.error("System error:{},{}", e.getMessage(), e.getStackTrace());
        }

        try {
            elasticsearchService.sendLog(host, results);
        } catch (Exception e) {
            log.info("{}:{}", e.getMessage(), e.getStackTrace());
        }
    }

}
