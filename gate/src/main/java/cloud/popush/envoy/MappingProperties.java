package cloud.popush.envoy;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;


@Configuration
@Data
@ConfigurationProperties(prefix = "gate")
@Validated
public class MappingProperties {
    @NotNull
    private Map<String, Element> filter;

    @Builder
    @Data
    @AllArgsConstructor
    @Validated
    public static class Element {
        @Singular
        private Map<String, Map<String, Boolean>> postMethods;

        @Singular
        private Map<String, Map<String, Boolean>> getMethods;
    }
}
