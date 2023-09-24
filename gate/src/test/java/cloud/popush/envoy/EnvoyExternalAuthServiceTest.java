package cloud.popush.envoy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MappingProperties.class)
class EnvoyExternalAuthServiceTest {

    @Autowired
    MappingProperties mappingProperties;

    @Test
    void getTargetFilterNames() {
        var service = new EnvoyExternalAuthService(List.of(), mappingProperties);
        var result = service.getActiveFilterNames("httpbin.local", "/spr/xx/geso", "get");

        Assertions.assertEquals(result, List.of("IpFilter"));
    }
}