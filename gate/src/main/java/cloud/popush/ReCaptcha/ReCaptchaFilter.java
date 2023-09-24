package cloud.popush.ReCaptcha;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.MachineException;
import cloud.popush.util.CheckRequestUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class ReCaptchaFilter implements GateFilter {

    @Value("${recaptcha.secret}")
    private String reCaptchaV3Secret;

    private static final Float threshold = 0.8F;

    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        Map<String, String> queryMap;
        try {
            queryMap = CheckRequestUtils.getQuery(checkRequest);
        } catch (UnsupportedEncodingException e) {
            return new AuthResultNg("Invalid character:%s".formatted(e.getMessage()));
        }

        if (!queryMap.containsKey("reCapchaToken")) {
            return new AuthResultNg("recaptcha token not found in body");
        }

        var recaptchaToken = queryMap.get("reCapchaToken");

        RestTemplate restTemplate = new RestTemplate();
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(
                        "https://www.google.com/recaptcha/api/siteverify")
                .queryParam("secret", reCaptchaV3Secret)
                .queryParam("response", recaptchaToken)
                .encode()
                .toUriString();

        ResponseEntity<ReCaptchaDto> response = restTemplate
                .exchange(urlTemplate,
                        HttpMethod.POST,
                        null,
                        ReCaptchaDto.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return new AuthResultNg("Recaptcha API Error");
        }

        if (Objects.isNull(response.getBody()) || !response.getBody().isSuccess()) {
            return new AuthResultNg("Recaptcha Response Error:%s".formatted(response.getBody().getErrorCodes()));
        }

        var score = response.getBody().getScore();
        if (score == null || score <= threshold) {
            return new AuthResultNg("Bad score: [%s] <= %s".formatted(score, threshold));
        }

        return new AuthReasonOk();
    }
}
