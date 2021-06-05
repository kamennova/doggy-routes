package com.kamennova.doggies;

import com.kamennova.doggies.dog.DogRepository;
import com.kamennova.doggies.dog.DogService;
import com.kamennova.doggies.route.RouteService;
import com.kamennova.doggies.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    public DogService dogService;

    @Autowired
    public DogRepository dogRepository;

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
        model.addAttribute("breeds", dogService.getBreedsInfo());

        return "dogs";
    }

    @GetMapping("my-routes")
    public String myRoutes(Model model, @AuthenticationPrincipal User principal) {
        if (principal == null) {
            return "";
        }

        model.addAttribute("userEmail", principal.getEmail());
        final List<HashMap<String, String>> routesInfo = routeService.getRoutesInfoOfUser(principal.getId());
        model.addAttribute("routes", routesInfo);
        model.addAttribute("areActive", dogRepository.userHasDogs(principal.getId()));

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
    public String signIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String errorMessage;

        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }

        return "signIn";
    }
}
