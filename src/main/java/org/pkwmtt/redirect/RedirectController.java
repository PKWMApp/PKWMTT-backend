package org.pkwmtt.redirect;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RedirectController {

    /**
     * Redirects to Swagger UI
     */
    @RequestMapping("/")
    public String redirectFromEmptyPathToSwagger(){
        return "redirect:/swagger-ui/index.html";
    }
}
