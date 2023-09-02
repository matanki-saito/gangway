package cloud.popush.envoy;

public class AuthReasonOk extends AuthResult {
    public AuthReasonOk() {
        this.reason = "Ok";
        this.isOk = true;
    }
}
