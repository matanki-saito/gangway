package cloud.popush.ip;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.ArgumentException;
import cloud.popush.exception.MachineException;
import cloud.popush.util.CheckRequestUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class IpFilter implements GateFilter {
    private final IpEntityMapper ipEntityMapper;

    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        String ipStr;
        try {
            ipStr = CheckRequestUtils.getIpStr(checkRequest);
        } catch (ArgumentException e) {
            return new AuthResultNg(e.getMessage());
        }

        if (ipEntityMapper.exist(ipStr)) {
            return new AuthResultNg("IP(%s) is on the rejection list.".formatted(ipStr));
        }

        return new AuthReasonOk("IP:%s".formatted(ipStr));
    }
}
