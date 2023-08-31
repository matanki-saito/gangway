package cloud.popush.country;

import cloud.popush.envoy.GateFilter;
import cloud.popush.util.GepIp2Service;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CountryFilter implements GateFilter {
    private final GepIp2Service geoIp2Service;
    private final CountryEntityMapper countryEntityMapper;

    @Override
    public boolean check(CheckRequest checkRequest) {
        var ipStr = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHost();

        var countryName = geoIp2Service.getCountryName(ipStr);

        return countryEntityMapper.exist(countryName);
    }
}
