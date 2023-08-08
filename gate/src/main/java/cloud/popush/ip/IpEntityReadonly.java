package cloud.popush.ip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpEntityReadonly extends IpEntity {
    private long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
