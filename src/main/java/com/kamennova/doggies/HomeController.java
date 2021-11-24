package com.kamennova.doggies;

import com.kamennova.doggies.dog.DogService;
import com.kamennova.doggies.dog.response.DogOverview;
import com.kamennova.doggies.route.RouteService;
import com.kamennova.doggies.route.response.PublicRouteOverview;
import com.kamennova.doggies.user.User;
import com.kamennova.doggies.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.GeneralSecurityException;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    public DogService dogService;

    @Autowired
    public RouteService routeService;

    @Autowired
    public UserService userService;

    @GetMapping(value = {"/", "/index"})
    public String index(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("isSignedIn", user != null);

        final List<DogOverview> dogs = dogService.getDogsOverview();
        final List<PublicRouteOverview> routes = routeService.getMapOverview();

        model.addAttribute("dogs", dogs);
        model.addAttribute("dogsCount", dogs.size());
        model.addAttribute("overview", routes);
        model.addAttribute("routesCount", routes.size());

        return "index";
    }

    @GetMapping("/my-dogs")
    public String myDogs(Model model, @AuthenticationPrincipal User principal) {
        model.addAttribute("userEmail", principal.getEmail());
        model.addAttribute("dogs", dogService.getDogsInfoOfOwner(principal.getId()));
        model.addAttribute("breeds", dogService.getBreedsInfo());

        return "dogs";
    }

    @GetMapping("my-routes")
    public String myRoutes(Model model, @AuthenticationPrincipal User principal) throws GeneralSecurityException {
        model.addAttribute("userEmail", principal.getEmail());

        byte[] address = principal.getAddressEncoded();
        model.addAttribute("userAddress", address.length > 0 ? userService.decodeUserAddress(address) : null);
        model.addAttribute("routes", routeService.getRoutesInfoOfUser(principal.getId()));

        return "routes";
    }

    @GetMapping("/signUp")
    public String signUp() {
        return "signUp";
    }

    @GetMapping("/signIn")
    public String signIn(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                model.addAttribute("error", ex.getMessage());
            }
        }

        return "signIn";
    }
}
