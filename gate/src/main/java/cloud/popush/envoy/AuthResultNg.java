package cloud.popush.envoy;

public class AuthResultNg extends AuthResult {
    public AuthResultNg(String reason) {
        this.reason = reason;
        this.isOk = false;
    }
}
