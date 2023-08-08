package cloud.popush.filter;

import cloud.popush.db.FingerprintEntityMapper;
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
                .getOrDefault("fingerprint", "");

        return fingerprintEntityMapper.exist(fingerprint);
    }
}
