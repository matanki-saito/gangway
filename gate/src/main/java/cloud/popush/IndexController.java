package cloud.popush;


import cloud.popush.exception.ArgumentException;
import cloud.popush.exception.OtherSystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    @Transactional(readOnly = true)
    @GetMapping("/")
    public String getIndex(
            Model model,
            Pageable pageable
    ) throws OtherSystemException, ArgumentException {
        return "index";
    }
}