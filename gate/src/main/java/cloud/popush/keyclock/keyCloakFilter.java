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
        var cookies = CheckRequestUtils.getCookies(checkRequest);

        var url = getUrl(checkRequest);

        var redirector = Map.of("Location", "/oauth2/authorization/keycloak?begin=%s".formatted(url));

        if (cookies.isEmpty()) {
            return new AuthResultNg("Not found cookie in request header",
                    redirector,
                    302);
        }

        if (!cookies.containsKey("far-caress")) {
            return new AuthResultNg("Not found key in cookie",
                    redirector,
                    302);
        }

        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(cookies.get("far-caress"));
        } catch (JwtException e) {
            return new AuthResultNg("Invalid JWT in cookie",
                    redirector,
                    302);
        }

        // TODO check scope in jwt

        return new AuthReasonOk("OK");
    }
}
