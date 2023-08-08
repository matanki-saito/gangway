package cloud.popush.filter;

import cloud.popush.vpngate.VpnGateDto;
import cloud.popush.vpngate.VpnGateService;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class VpnGateFilter implements GateFilter {
    private final VpnGateService vpnGateService;

    @Override
    public boolean check(CheckRequest checkRequest) {
        var ipStr = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHost();

        return vpnGateService.getList()
                .stream()
                .map(VpnGateDto::getIp)
                .anyMatch(ipStr::equals);
    }
}
