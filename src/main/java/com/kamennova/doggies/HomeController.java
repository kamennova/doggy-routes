package com.kamennova.doggies;

import com.kamennova.doggies.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class HomeController {
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/")
    public String indexShort(Model model) {
        model.addAttribute("msg", "Howdy, World");
        return "index";
    }

    @GetMapping("/my-dogs")
    public String myDogs(Model model, @AuthenticationPrincipal User principal) {
        System.out.println(principal);
        final String email = (principal != null ? principal.getEmail() : "default");
        model.addAttribute("userEmail", email);

        if(principal != null){
            model.addAttribute("dogs", principal.getDogs());
        }

        return "dogs";
    }

    @GetMapping("my-routes")
    public String myRoutes(Model model, Principal principal) {
        return "routes";
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/signUp")
    public String signUp() {
        return "signUp";
    }

    @GetMapping("/signIn")
    public String signIn() {
        return "signIn";
    }
}
