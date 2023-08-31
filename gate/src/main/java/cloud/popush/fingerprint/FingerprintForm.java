package cloud.popush.fingerprint;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FingerprintForm {
    @NotNull
    @Pattern(regexp = "^[a-z0-9]{32}$")
    private String key;
}
