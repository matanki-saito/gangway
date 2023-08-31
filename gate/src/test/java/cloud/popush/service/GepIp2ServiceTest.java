package cloud.popush.service;

import cloud.popush.util.GepIp2Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GepIp2ServiceTest {

    @InjectMocks
    private GepIp2Service gepIp2Service;

    @Test
    void getAsnName() {
        var org = gepIp2Service.getAsnName("90.149.116.17");
        Assertions.assertEquals("Sony Network Communications Inc.", org);
    }

    @Test
    void getCountryName() {
        var org = gepIp2Service.getCountryName("90.149.116.17");
        Assertions.assertEquals("Japan", org);
    }
}