package cloud.popush.useragent;

import cloud.popush.envoy.AuthResult;
import io.envoyproxy.envoy.service.auth.v3.AttributeContext;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UserAgentFilterTest {

    @Test
    void checkF() throws Exception {
        var target = new UserAgentFilter();
        AuthResult result = target.check(CheckRequest.newBuilder().setAttributes(
                AttributeContext.newBuilder()
                        .setRequest(AttributeContext.Request.newBuilder()
                                .setHttp(AttributeContext.HttpRequest.newBuilder()
                                        .putHeaders("User-Agent",
                                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/9D013D")
                                        .build())
                                .build()).build()
        ).build());

        Assertions.assertThat(result.isOk()).isFalse();
    }

    @Test
    void checkT() throws Exception {
        var target = new UserAgentFilter();
        AuthResult result = target.check(CheckRequest.newBuilder().setAttributes(
                AttributeContext.newBuilder()
                        .setRequest(AttributeContext.Request.newBuilder()
                                .setHttp(AttributeContext.HttpRequest.newBuilder()
                                        .putHeaders("User-Agent",
                                                "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36")
                                        .build())
                                .build()).build()
        ).build());

        Assertions.assertThat(result.isOk()).isTrue();
    }
}