package cloud.popush.proxy;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.ArgumentException;
import cloud.popush.exception.MachineException;
import cloud.popush.exception.OtherSystemException;
import cloud.popush.util.CheckRequestUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProxyCheckFilter implements GateFilter {

    private final VpnapiService vpnapiService;

    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        String ipStr;
        try {
            ipStr = CheckRequestUtils.getIpStr(checkRequest);
        } catch (ArgumentException e) {
            return new AuthResultNg(e.getMessage());
        }

        VpnapiDto info;
        try {
            info = vpnapiService.getInfo(ipStr);
        } catch (OtherSystemException e) {
            return new AuthResultNg(e.getMessage());
        }

        // internal API
        if (info.getSecurity() == null) {
            return new AuthReasonOk();
        }

        if (info.getSecurity().isProxy()) {
            return new AuthResultNg("Detect proxy");
        } else if (info.getSecurity().isTor()) {
            return new AuthResultNg("Detect tor");
        } else if (info.getSecurity().isVpn()) {
            return new AuthResultNg("Detect VPN");
        } else if (info.getSecurity().isRelay()) {
            return new AuthResultNg("Detect Relay");
        } else {
            return new AuthReasonOk();
        }
    }
}
