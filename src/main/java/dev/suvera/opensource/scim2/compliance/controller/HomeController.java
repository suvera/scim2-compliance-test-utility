package dev.suvera.opensource.scim2.compliance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * author: suvera
 * date: 9/3/2020 1:56 PM
 */
@Controller
public class HomeController {
    private final String LAYOUT = "layout/default";

    @GetMapping({"/home", "/"})
    public String home(Model model) {
        model.addAttribute("view", "home");
        return LAYOUT;
    }

    @GetMapping("/help")
    public String help(Model model) {
        model.addAttribute("view", "help");
        return LAYOUT;
    }

}
