package cloud.popush.fingerprint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FingerprintEntityCondition {
    private Long id;
    private String key;
}
