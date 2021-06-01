package com.kamennova.doggies;

import com.kamennova.doggies.dog.DogService;
import com.kamennova.doggies.route.RouteService;
import com.kamennova.doggies.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    public DogService dogService;

    @Autowired
    public RouteService routeService;

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/my-dogs")
    public String myDogs(Model model, @AuthenticationPrincipal User principal) {
        if (principal == null) return "signIn";

        model.addAttribute("userEmail", principal.getEmail());
        model.addAttribute("dogs", dogService.getDogsInfoOfOwner(principal.getId()));

        return "dogs";
    }

    @GetMapping("my-routes")
    public String myRoutes(Model model, @AuthenticationPrincipal User principal) {
        if (principal == null) {
            return "signIn";
        }

        model.addAttribute("userEmail", principal.getEmail());
        model.addAttribute("routes", routeService.getRoutesInfoOfUser(principal.getId()));

        return "routes";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
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
