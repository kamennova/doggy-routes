package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    @Autowired
    RouteService service;

    @Autowired
    RouteRepository repository;

    @PostMapping(value = "", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@RequestBody RouteReq body, @AuthenticationPrincipal User user) {
        final Map<String, Object> res = new HashMap<>();

        final List<DoubleCoordinate> coordinates = service.foldToCoordinates(body.coordinates);
        final int length = service.getRouteLength(coordinates);
        final String error = service.validateCoordinates(coordinates, length);

        if (!error.isEmpty()) {
            res.put("error", error);
            return ResponseEntity.badRequest().body(res);
        }

        final Route route = new Route(coordinates, user, length);
        repository.save(route);

        res.put("id", route.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("")
    ResponseEntity<Map<String, Object>> overview() {
        Map<String, Object> res = new HashMap<>();
        res.put("overview", service.getMapOverview());

        return ResponseEntity.ok(res);
    }

    @GetMapping("/details")
    ResponseEntity<Map<String, Object>> details(@RequestParam Double lat, @RequestParam Double lng) {
        Map<String, Object> res = new HashMap<>();

        final DoubleCoordinate mapCenter = new DoubleCoordinate(lat, lng);
        final String centerError = service.validateCoordinate(mapCenter);

        if (!centerError.isEmpty()) {
            res.put("error", centerError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        final List<Route> routes = service.findWithFilter(mapCenter);
        res.put("lines", service.getDetailedMap(routes));

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
