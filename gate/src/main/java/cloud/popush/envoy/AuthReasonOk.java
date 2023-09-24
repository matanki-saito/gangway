package cloud.popush.envoy;

import java.util.Map;

public class AuthReasonOk extends AuthResult {
    private void init() {
        this.reason = "Ok";
        this.isOk = true;
    }

    public AuthReasonOk() {
        init();
    }

    public AuthReasonOk(String reason) {
        init();
        this.reason = reason;
    }

    public AuthReasonOk(Map<String, Object> context) {
        init();
        this.context = context;
    }
}
