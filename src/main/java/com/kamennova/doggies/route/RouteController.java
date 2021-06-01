package com.kamennova.doggies.route;

import com.kamennova.doggies.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    RouteService service;
    RouteRepository repository;

    RouteController(RouteService service, RouteRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping(value = "", consumes = "application/json")
    @ResponseBody
    public HashMap<String, String> newRoute(@RequestBody RouteRequest body, @AuthenticationPrincipal User user) {
        final HashMap<String, String> res = new HashMap<>();

        final List<Coordinate> coords = service.foldToCoordinates(body.coords);
        final String coordsError = service.validateCoordinates(coords);

        if (!coordsError.isEmpty()) {
            res.put("error", coordsError);
            return res;
        }

        final Route route = new Route(coords, !user.getDogs().isEmpty());
        repository.save(route);

        res.put("status", "ok");
        res.put("id", route.getId().toString());
        return res;
    }

    @GetMapping("")
    Set<Route> findRoutesNear(@RequestParam float lat, @RequestParam float lng) {
        return service.findActiveNear(new Coordinate(lat, lng));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteRoute(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
