package cloud.popush.envoy;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AuthResultNg extends AuthResult {
    String responseMessage = "";
    Integer responseStatusCode = 403;
    Map<String, String> responseHeader = new HashMap<>();

    private void init() {
        this.isOk = false;
    }

    public AuthResultNg(String reason) {
        init();
        this.reason = reason;
    }

    public AuthResultNg(String reason, Map<String, String> responseHeader, Integer responseStatusCode) {
        init();
        this.responseHeader = responseHeader;
        this.responseStatusCode = responseStatusCode;
    }
}
