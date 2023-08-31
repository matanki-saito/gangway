package cloud.popush.vpngate;

import cloud.popush.envoy.GateFilter;
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
                .noneMatch(ipStr::equals);
    }
}
