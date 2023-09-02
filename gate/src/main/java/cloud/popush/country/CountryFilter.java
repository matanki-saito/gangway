package cloud.popush.country;

import cloud.popush.envoy.AuthReasonOk;
import cloud.popush.envoy.AuthResult;
import cloud.popush.envoy.AuthResultNg;
import cloud.popush.envoy.GateFilter;
import cloud.popush.exception.ArgumentException;
import cloud.popush.exception.MachineException;
import cloud.popush.util.GepIp2Service;
import cloud.popush.util.NetUtils;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class CountryFilter implements GateFilter {
    private final GepIp2Service geoIp2Service;
    private final CountryEntityMapper countryEntityMapper;

    @Override
    public AuthResult check(CheckRequest checkRequest) throws MachineException {

        String ipStr;

        try {
            ipStr = NetUtils.getIpStr(checkRequest);
        } catch (ArgumentException e) {
            return new AuthResultNg(e.getMessage());
        }

        String countryName;

        // system machine
        if (ipStr.equals("192.168.1.200")) {
            return new AuthReasonOk();
        }

        try {
            countryName = geoIp2Service.getCountryName(ipStr);
        } catch (ArgumentException e) {
            return new AuthResultNg("IP(%s) to country code conversion failed;error=%s"
                    .formatted(ipStr, e.getMessage()));
        }

        if (!countryEntityMapper.exist(countryName)) {
            return new AuthResultNg("No country name(%s) on the permit list. Ref:IP=%s"
                    .formatted(countryName, ipStr));
        }

        return new AuthReasonOk();
    }
}
