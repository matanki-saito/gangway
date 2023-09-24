package cloud.popush.envoy;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class AuthResult {
    boolean isOk;
    String reason = "";
    String filterName = "";
    Map<String, Object> context = new HashMap<>();
}
