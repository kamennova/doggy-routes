package com.kamennova.doggies.route;

import com.kamennova.doggies.dog.DogRepository;
import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    @Autowired
    RouteService service;

    @Autowired
    RouteRepository repository;

    @Autowired
    DogRepository dogRepository;

    @PostMapping(value = "", consumes = "application/json")
    @ResponseBody
    public HashMap<String, Object> newRoute(@RequestBody RouteRequest body, @AuthenticationPrincipal User user) {
        final HashMap<String,Object> res = new HashMap<>();
        final List<DoubleCoordinate> coords = service.foldToCoordinates(body.coordinates);
        final int length = service.getRouteLength(coords);
        final String coordsError = service.validateCoordinates(coords, length);

        if (!coordsError.isEmpty()) {
            res.put("error", coordsError);
            return res;
        }

        final boolean isRouteActive = dogRepository.userHasDogs(user.getId());
        final Route route = new Route(coords, user, isRouteActive, length);
        repository.save(route);

        res.put("status", "ok");
        res.put("id", route.getId());
        return res;
    }

    @GetMapping("")
    HashMap<String, Object> findRoutesNear(@RequestParam Double lat, @RequestParam Double lng, @RequestParam Float zoom) {
        HashMap<String, Object> res = new HashMap<>();

        if (zoom < 4.5) {
            res.put("overview", service.getRoutesOverview());
        } else {
            res.put("lines", service.findRoutesNear(new DoubleCoordinate(lat, lng)));
        }

        return res;
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    Model deleteRoute(@PathVariable Long id, Model model) {
        repository.deleteById(id);
        model.addAttribute("status", "ok");

        return model;
    }
}
