package cloud.popush.envoy;


import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;

@GRpcServiceAdvice
@Slf4j
public class EnvoyGrpcExceptionHandler {
    @GRpcExceptionHandler
    public Status handleGeneralArgument(Exception e, GRpcExceptionScope scope) {
        log.info("error!");
        return Status.INVALID_ARGUMENT.withDescription(e.getMessage());
    }
}
