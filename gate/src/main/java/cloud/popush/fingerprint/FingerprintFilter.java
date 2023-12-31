package cloud.popush.fingerprint;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.MachineException;
import cloud.popush.util.CheckRequestUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@AllArgsConstructor
@Component
public class FingerprintFilter implements GateFilter {

    private final FingerprintEntityMapper fingerprintEntityMapper;

    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {

        Map<String, Object> queryMap;
        try {
            queryMap = CheckRequestUtils.getQuery(checkRequest);
        } catch (UnsupportedEncodingException e) {
            return new AuthResultNg("Invalid character: %s".formatted(e.getMessage()));
        }

        if (!queryMap.containsKey("fingerprint")) {
            return new AuthResultNg("fingerprint not found in body");
        }

        var fingerprint = queryMap.get("fingerprint").toString();
        if (fingerprintEntityMapper.exist(fingerprint)) {
            return new AuthResultNg("fingerprint is on the rejection list.");
        }

        return new AuthReasonOk(Map.of("user.fingerprint", fingerprint));
    }
}
