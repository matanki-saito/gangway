package cloud.popush.service;

import cloud.popush.filter.GateFilter;
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

        for (var filter : gateFilterList) {
            if (!filter.check(request)) {
                log.debug("NG:%s".formatted(filter.getClass().getName()));
                isPass = false;
                break;
            }
        }

        var r = CheckResponse.newBuilder()
                .setStatus(Status
                        .newBuilder()
                        .setCode(isPass ? Code.OK_VALUE : Code.PERMISSION_DENIED_VALUE)
                        .setMessage(isPass ? "OK" : "NG")
                        .build())
                .build();

        responseObserver.onNext(r);
        responseObserver.onCompleted();
    }
}
