package cloud.popush.envoy;

import cloud.popush.exception.MachineException;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;

public interface GateFilter {
    AuthResult check(CheckRequest checkRequest) throws MachineException;
}
