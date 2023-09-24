package cloud.popush.keyclock;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.MachineException;
import cloud.popush.util.CheckRequestUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

import static cloud.popush.util.CheckRequestUtils.getUrl;

@RequiredArgsConstructor
@Component
public class keyCloakFilter implements GateFilter {
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        var cookies = CheckRequestUtils.getCookie(checkRequest);

        var url = getUrl(checkRequest);

        var redirector = Map.of("Location", "/ido-front?url=%s".formatted(url));

        if (cookies.isEmpty()) {
            return new AuthResultNg("Not found cookie in request header",
                    redirector,
                    302);
        }

        var targetHeader = cookies
                .get()
                .stream()
                .filter(x -> "far-caress".equals(x.getName()))
                .findAny();

        if (targetHeader.isEmpty()) {
            return new AuthResultNg("Not found key in cookie",
                    redirector,
                    302);
        }

        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(targetHeader.get().getValue());
        } catch (JwtException e) {
            return new AuthResultNg("Invalid JWT in cookie",
                    redirector,
                    302);
        }

        // TODO check scope in jwt

        return new AuthReasonOk("OK");
    }
}
