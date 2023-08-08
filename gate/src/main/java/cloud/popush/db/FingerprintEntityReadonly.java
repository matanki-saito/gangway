package cloud.popush.db;

import cloud.popush.db.FingerprintEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FingerprintEntityReadonly extends FingerprintEntity {
    private long fingerprintId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
