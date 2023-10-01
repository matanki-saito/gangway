package cloud.popush.logging;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.GateFilter;
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
@Order(1)
public class TargetFilter implements GateFilter {
    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        Map<String, Object> result = new HashMap<>();

        var host = CheckRequestUtils.getHost(checkRequest);
        result.put("target.host", host);

        var path = CheckRequestUtils.getPath(checkRequest);
        result.put("target.path", path);

        var scheme = CheckRequestUtils.getScheme(checkRequest);
        result.put("target.scheme", scheme);

        var method = CheckRequestUtils.getMethod(checkRequest);
        result.put("target.method", method);

        return new AuthReasonOk(result);
    }
}

