package cloud.popush.service;

import cloud.popush.vpngate.VpnGateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VpnGateServiceTest {

    @InjectMocks
    private VpnGateService vpnGateService;

    @Test
    void getList() {
        var list = vpnGateService.getList();
        Assertions.assertEquals("73.158.107.75", list.get(0).getIp());
    }
}