package com.highway.etc.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RootController {

    @GetMapping({"/", ""})
    public RedirectView index() {
        // 直接跳转到 Swagger UI，避免根路径 404
        return new RedirectView("/swagger-ui.html");
    }
}
