package cloud.popush.envoy;

import io.envoyproxy.envoy.service.auth.v3.CheckRequest;

public interface GateFilter {
    boolean check(CheckRequest checkRequest);
}
