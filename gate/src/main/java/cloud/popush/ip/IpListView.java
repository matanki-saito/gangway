package cloud.popush.ip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IpListView {
    private Page<Element> pageData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Element {
        private long id;
        private String ip;
    }
}
