package cloud.popush;

import com.google.rpc.Status;
import io.envoyproxy.envoy.config.core.v3.Address;
import io.envoyproxy.envoy.service.auth.v3.AttributeContext;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Test;
import org.lognet.springboot.grpc.context.LocalRunningGrpcPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = GateApplication.class,
        properties = "grpc.port=0" // ランダムポート
)
@ActiveProfiles("test")
public class GateApplicationTest {
    @LocalRunningGrpcPort // 起動したサーバのportをinjectしてくれる
    private int runningPort;

    @Test
    void test() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", runningPort)
                .usePlaintext()
                .build();

        var stub = AuthorizationGrpc.newBlockingStub(channel);

        var request = CheckRequest.newBuilder()
                .setAttributes(AttributeContext
                        .newBuilder()
                        .setSource(AttributeContext.Peer
                                .newBuilder()
                                .setAddress(Address
                                        .newBuilder()
                                        .build())
                                .build())
                        .setRequest(AttributeContext.Request.newBuilder()
                                .setHttp(AttributeContext.HttpRequest.newBuilder()
                                        .setMethod("GET")
                                        .setScheme("http")
                                        .setHost("90.149.116.17")
                                        .setBody("")
                                        .putHeaders("fingerprint", "1")
                                        .setId("12412313")
                                        .build())
                                .build())
                        .build())
                .build();
        var actual = stub.check(request).getStatus();
        var expected = Status.newBuilder()
                .setCode(0)
                .setMessage("OK")
                .build();
        assertThat(actual).isEqualTo(expected);
    }
}
