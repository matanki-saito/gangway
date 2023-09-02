package cloud.popush.vpngate;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.ArgumentException;
import cloud.popush.exception.MachineException;
import cloud.popush.util.NetUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
//@Component
public class VpnGateFilter implements GateFilter {
    private final VpnGateService vpnGateService;

    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {
        String ipStr;
        try {
            ipStr = NetUtils.getIpStr(checkRequest);
        } catch (ArgumentException e) {
            return new AuthResultNg(e.getMessage());
        }

        if (vpnGateService.getList()
                .stream()
                .map(VpnGateDto::getIp)
                .anyMatch(ipStr::equals)) {
            return new AuthResultNg("IP(%s) is on the VPNGate list.".formatted(ipStr));
        }

        return new AuthReasonOk();
    }
}
