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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Component
@Order(1)
public class RequestLogFilter implements GateFilter {

    public AuthResult check(CheckRequest checkRequest) throws MachineException {

        Map<String, Object> result = new HashMap<>();

        result.put("host", CheckRequestUtils.getHost(checkRequest));

        result.put("timestamp", LocalDateTime.now(ZoneId.of("UTC")));

        result.put("path", CheckRequestUtils.getPath(checkRequest));

        result.put("method", CheckRequestUtils.getMethod(checkRequest));

        return new AuthReasonOk(result);
    }
}
