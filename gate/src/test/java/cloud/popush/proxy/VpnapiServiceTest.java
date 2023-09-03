package cloud.popush.proxy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VpnapiServiceTest {

    @Test
    void getInfo() throws Exception{
        var service = new VpnapiService("");

        var info = service.getInfo("1.33.233.231");

        Assertions.assertThat(info.getIp()).isEqualTo("1.33.233.231");
    }
}