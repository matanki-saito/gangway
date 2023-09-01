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

        log.info("{}", request);

        try {
            for (var filter : gateFilterList) {
                if (!filter.check(request)) {
                    log.info("NG:%s".formatted(filter.getClass().getName()));
                    isPass = false;
                    break;
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            log.info("{}", e.getStackTrace());
        }

        log.info("f");

        var r = CheckResponse.newBuilder()
                .setStatus(Status
                        .newBuilder()
                        .setCode(isPass ? Code.OK_VALUE : Code.PERMISSION_DENIED_VALUE)
                        .setMessage(isPass ? "OK" : "NG")
                        .build())
                .build();

        log.info("{}", r);

        responseObserver.onNext(r);
        responseObserver.onCompleted();
        responseObserver.onError(new RuntimeException());
    }
}
