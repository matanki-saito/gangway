package cloud.popush.envoy;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@GRpcService
public class EnvoyExternalAuthService extends AuthorizationGrpc.AuthorizationImplBase {

    private final List<GateFilter> gateFilterList;

    @Override
    public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
        var isPass = true;

        try {
            for (var filter : gateFilterList) {
                var result = filter.check(request);
                if (!result.isOk) {
                    isPass = false;
                    log.info("{}:{}", filter.getClass(), result.reason);
                    break;
                }
            }
        } catch (Exception e) {
            isPass = false;
            log.warn("System error:{},{}", e.getMessage(), e.getStackTrace());
        }

        if (isPass) {
            log.info("PASS");
        }

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
}
