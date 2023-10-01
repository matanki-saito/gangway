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

        Map<String, Object> query;
        try {
            query = CheckRequestUtils.getQuery(checkRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> contexts = new HashMap<>();
        if (query.containsKey("plugin")) {
            contexts.put("pukiwiki.plugin", query.get("plugin").toString());
        }
        if (query.containsKey("cmd")) {
            contexts.put("pukiwiki.cmd", query.get("cmd").toString());
        }
        if (query.containsKey("page")) {
            contexts.put("pukiwiki.page", StringUtils.truncate(query.get("page").toString(), 1500));
        }
        if (query.containsKey("msg")) {
            contexts.put("pukiwiki.msg", StringUtils.truncate(query.get("msg").toString(), 1500));
        }
        if (query.containsKey("original")) {
            contexts.put("pukiwiki.original", StringUtils.truncate(query.get("original").toString(), 1500));
        }

        return new AuthReasonOk(contexts);
    }
}
