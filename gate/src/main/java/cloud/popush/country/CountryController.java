package cloud.popush.country;

import cloud.popush.exception.ArgumentException;
import cloud.popush.exception.OtherSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CountryController {
    private final CountryEntityMapper countryEntityMapper;

    @GetMapping("/country")
    public String getCountryList(
            @Validated CountrySearchForm countrySearchForm,
            Model model,
            Pageable pageable
    ) throws OtherSystemException, ArgumentException {

        var item100 = countryEntityMapper.select(CountryEntityCondition
                .builder()
                .name(countrySearchForm.getName())
                .build());

        var item100list = item100.stream()
                .map(x -> CountryListView
                        .Element
                        .builder()
                        .id(x.getId())
                        .name(x.getName())
                        .build())
                .toList();

        // pageableに合わせてsliceする
        int beginIndex = (int) pageable.getOffset();
        int endIndex = Math.min(beginIndex + pageable.getPageSize(), item100list.size());

        model.addAttribute("countrySearchForm", new CountrySearchForm());
        model.addAttribute("countryForm", new CountryForm());
        model.addAttribute("countryListView",
                CountryListView.builder()
                        .pageData(new PageImpl<>(
                                item100list.subList(beginIndex, endIndex),
                                pageable,
                                item100list.size()))
                        .build());

        return "country";
    }

    @Transactional
    @PostMapping("/country/register")
    public String registerIp(
            @Validated CountryForm countryForm
    ) throws OtherSystemException, ArgumentException {
        countryEntityMapper.insert(CountryEntity.builder().name(countryForm.getName()).build());
        return "redirect:../country";
    }

    @Transactional
    @PostMapping("/country/delete/{id}")
    public String deleteIp(
            @PathVariable("id") long id
    ) throws OtherSystemException, ArgumentException {
        countryEntityMapper.delete(id);
        return "redirect:../../country";
    }
}
