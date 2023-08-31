package cloud.popush.country;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryEntityReadonly extends CountryEntity {
    private long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
