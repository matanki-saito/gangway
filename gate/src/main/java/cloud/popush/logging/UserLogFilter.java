package cloud.popush.logging;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.ArgumentException;
import cloud.popush.exception.MachineException;
import cloud.popush.util.CheckRequestUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Component
@Order(0)
public class UserLogFilter implements GateFilter {
    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        Map<String, Object> result = new HashMap<>();

        String userAgent;
        try {
            userAgent = CheckRequestUtils.getUserAgent(checkRequest);
        } catch (ArgumentException e) {
            userAgent = "Invalid";
        }
        result.put("userAgent", userAgent);

        String ip;
        try {
            ip = CheckRequestUtils.getIpStr(checkRequest);
        } catch (ArgumentException e) {
            ip = "Invalid";
        }
        result.put("IP", ip);

        // TODO jwt

        return new AuthReasonOk(result);
    }
}
