package com.kamennova.doggies.route;

import com.kamennova.doggies.dog.DogRepository;
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

    @Autowired
    DogRepository dogRepository;

    @PostMapping(value = "", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@RequestBody RouteReq body, @AuthenticationPrincipal User user) {
        final Map<String, Object> res = new HashMap<>();

        final List<DoubleCoordinate> coords = service.foldToCoordinates(body.coordinates);
        final int length = service.getRouteLength(coords);
        final String coordsError = service.validateCoordinates(coords, length);

        if (!coordsError.isEmpty()) {
            res.put("error", coordsError);
            return ResponseEntity.badRequest().body(res);
        }

        final Route route = new Route(coords, user, length);
        repository.save(route);

        res.put("id", route.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("")
    ResponseEntity<Map<String, Object>> overview() {
        Map<String, Object> res = new HashMap<>();
        res.put("overview", service.getRoutesOverview());

        return ResponseEntity.ok(res);
    }

    @GetMapping("/details")
    ResponseEntity<Map<String, Object>> details(@RequestParam Double lat, @RequestParam Double lng) {
        Map<String, Object> res = new HashMap<>();

        final DoubleCoordinate mapCenter = new DoubleCoordinate(lat, lng);
        if (!RouteService.isInKyiv(mapCenter)) {
            res.put("error", "Координата знаходиться за межами Києва");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        final List<Route> routes = service.findWithFilter(mapCenter);
        res.put("lines", service.getRouteMaps(routes));
        res.put("dogs", service.getRoutesDogs(routes));

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
