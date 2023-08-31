package cloud.popush.fingerprint;

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
public class FingerprintController {
    private final FingerprintEntityMapper fingerprintEntityMapper;

    @GetMapping("/fingerprint")
    public String getFingerprintList(
            @Validated FingerprintSearchForm fingerprintSearchForm,
            Model model,
            Pageable pageable
    ) throws OtherSystemException, ArgumentException {

        var item100 = fingerprintEntityMapper.select(FingerprintEntityCondition
                .builder()
                .key(fingerprintSearchForm.getKey())
                .build());

        var item100list = item100.stream()
                .map(x -> FingerprintListView
                        .Element
                        .builder()
                        .id(x.getId())
                        .key(x.getKey())
                        .build())
                .toList();

        // pageableに合わせてsliceする
        int beginIndex = (int) pageable.getOffset();
        int endIndex = Math.min(beginIndex + pageable.getPageSize(), item100list.size());

        model.addAttribute("fingerprintSearchForm", new FingerprintSearchForm());
        model.addAttribute("fingerprintForm", new FingerprintForm());
        model.addAttribute("fingerprintListView",
                FingerprintListView.builder()
                        .pageData(new PageImpl<>(
                                item100list.subList(beginIndex, endIndex),
                                pageable,
                                item100list.size()))
                        .build());

        return "fingerprint";
    }

    @Transactional
    @PostMapping("/fingerprint/register")
    public String registerIp(
            @Validated FingerprintForm fingerprintForm
    ) throws OtherSystemException, ArgumentException {
        fingerprintEntityMapper.insert(FingerprintEntity.builder().key(fingerprintForm.getKey()).build());
        return "redirect:../fingerprint";
    }

    @Transactional
    @PostMapping("/fingerprint/delete/{id}")
    public String deleteIp(
            @PathVariable("id") long id
    ) throws OtherSystemException, ArgumentException {
        fingerprintEntityMapper.delete(id);
        return "redirect:../../fingerprint";
    }

}
