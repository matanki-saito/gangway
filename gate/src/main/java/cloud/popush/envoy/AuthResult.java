package cloud.popush.envoy;

import lombok.Getter;

@Getter
public abstract class AuthResult {
    boolean isOk;
    String reason;
}
