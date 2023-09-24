package cloud.popush.logging;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.MachineException;
import cloud.popush.util.CheckRequestUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Component
public class PukiwikiRequestLogFilter implements GateFilter {
    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        Map<String, Object> result = new HashMap<>();

        Map<String, String> query;
        try {
            query = CheckRequestUtils.getQuery(checkRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> contents = new HashMap<>();
        if (query.containsKey("plugin")) {
            contents.put("plugin", query.get("plugin"));
        }
        if (query.containsKey("cmd")) {
            contents.put("cmd", query.get("cmd"));
        }
        if (query.containsKey("page")) {
            contents.put("page", StringUtils.truncate(query.get("page"), 1500));
        }
        if (query.containsKey("msg")) {
            contents.put("msg", StringUtils.truncate(query.get("msg"), 1500));
        }
        if (query.containsKey("original")) {
            contents.put("original", StringUtils.truncate(query.get("original"), 1500));
        }

        result.put("content",contents);

        return new AuthReasonOk(result);
    }
}
