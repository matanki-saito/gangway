package cloud.popush;

import cloud.popush.service.EnvoyExternalAuthService;
import com.google.rpc.Status;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnvoyExternalAuthServiceTest {

    @InjectMocks
    private EnvoyExternalAuthService envoyExternalAuthService;

    @Mock
    private StreamObserver<CheckResponse> responseStreamObserver;

    @Test
    void check() {

        doNothing().when(responseStreamObserver).onNext(any());
        doNothing().when(responseStreamObserver).onCompleted();

        var request = CheckRequest.newBuilder()
                .build();

        envoyExternalAuthService.check(request, responseStreamObserver);

        var expected = CheckResponse.newBuilder()
                .setStatus(Status.newBuilder()
                        .setCode(0)
                        .setMessage("OK")
                        .build())
                .build();

        verify(responseStreamObserver).onNext(expected);
    }
}