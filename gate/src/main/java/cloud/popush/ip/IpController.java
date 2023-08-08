package cloud.popush.ip;

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
public class IpController {
    private final IpEntityMapper ipEntityMapper;

    @GetMapping("/ip")
    public String getIpList(
            @Validated IpSearchForm ipSearchForm,
            Model model,
            Pageable pageable
    ) throws OtherSystemException, ArgumentException {

        var item100 = ipEntityMapper.select(IpEntityCondition
                .builder()
                .string(ipSearchForm.getIp())
                .build());

        var item100list = item100.stream()
                .map(x -> IpListView
                        .Element
                        .builder()
                        .id(x.getId())
                        .ip(x.getString())
                        .build())
                .toList();

        // pageableに合わせてsliceする
        int beginIndex = (int) pageable.getOffset();
        int endIndex = Math.min(beginIndex + pageable.getPageSize(), item100list.size());

        model.addAttribute("ipSearchForm", new IpSearchForm());
        model.addAttribute("ipForm", new IpForm());
        model.addAttribute("ipListView",
                IpListView.builder()
                        .pageData(new PageImpl<>(
                                item100list.subList(beginIndex, endIndex),
                                pageable,
                                item100list.size()))
                        .build());

        return "ip";
    }

    @Transactional
    @PostMapping("/ip/register")
    public String registerIp(
            @Validated IpForm ipForm
    ) throws OtherSystemException, ArgumentException {
        ipEntityMapper.insert(IpEntity.builder().string(ipForm.getIp()).build());
        return "redirect:../ip";
    }

    @Transactional
    @PostMapping("/ip/delete/{id}")
    public String deleteIp(
            @PathVariable("id") long id
    ) throws OtherSystemException, ArgumentException {
        ipEntityMapper.delete(id);
        return "redirect:../../ip";
    }
}
