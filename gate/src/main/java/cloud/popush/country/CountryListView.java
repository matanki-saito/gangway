package cloud.popush.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryListView {
    private Page<Element> pageData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Element {
        private long id;
        private String name;
    }
}
