package be.e_contract.ejsf.spring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectToIndexController {

    @GetMapping("/")
    public String index() {
        return "redirect:index.xhtml";
    }
}
