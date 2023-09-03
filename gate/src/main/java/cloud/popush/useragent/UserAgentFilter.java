package cloud.popush.useragent;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.ArgumentException;
import cloud.popush.exception.MachineException;
import cloud.popush.util.NetUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@AllArgsConstructor
@Component
public class UserAgentFilter implements GateFilter {

    private static final Pattern rePattern = Pattern.compile("(.*Windows NT 6.1.*)|(Safari/[0-9A-Z]{6}$)");

    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        String userAgent;

        try {
            userAgent = NetUtils.getUserAgent(checkRequest);
        } catch (ArgumentException e) {
            return new AuthResultNg(e.getMessage());
        }

        if (rePattern.matcher(userAgent).find()) {
            return new AuthResultNg("Bad UA: %s".formatted(userAgent));
        }

        return new AuthReasonOk("UA:%s".formatted(userAgent));
    }
}
