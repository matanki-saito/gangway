package cloud.popush.ip;

import cloud.popush.envoy.GateFilter;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class IpFilter implements GateFilter {
    private final IpEntityMapper ipEntityMapper;

    @Override
    public boolean check(CheckRequest checkRequest) {
        var ipStr = checkRequest
                .getAttributes()
                .getRequest()
                .getHttp()
                .getHost();

        return !ipEntityMapper.exist(ipStr);
    }
}
