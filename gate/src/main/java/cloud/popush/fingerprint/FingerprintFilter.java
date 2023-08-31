package cloud.popush.fingerprint;

import cloud.popush.envoy.GateFilter;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class FingerprintFilter implements GateFilter {

    private final FingerprintEntityMapper fingerprintEntityMapper;

    @Override
    public boolean check(CheckRequest checkRequest) {
        var fingerprint = checkRequest.getAttributes()
                .getRequest()
                .getHttp()
                .getHeadersMap()
                .getOrDefault("fingerprint", "a1111111111111111111111111111111");

        return !fingerprintEntityMapper.exist(fingerprint);
    }
}
