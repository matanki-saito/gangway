package cloud.popush.fingerprint;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.MachineException;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class FingerprintFilter implements GateFilter {

    private final FingerprintEntityMapper fingerprintEntityMapper;

    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        var fingerprint = checkRequest.getAttributes()
                .getRequest()
                .getHttp()
                .getHeadersMap()
                .getOrDefault("fingerprint", "a1111111111111111111111111111111");


        if (fingerprintEntityMapper.exist(fingerprint)) {
            return new AuthResultNg("fp(%s) is on the rejection list.".formatted(fingerprint));
        }

        return new AuthReasonOk();
    }
}
