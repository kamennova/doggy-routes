package com.kamennova.doggies.route;

import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class RouteController {
    RouteService service;

    RouteController(RouteService service){
        this.service = service;
    }

    @PostMapping("/routes")
    Route newRoute(@RequestBody RouteRequest body){
        final String polylineStr = PolylineCoder.encodeCoordinates(body.coords);

        return new Route(polylineStr, body.coords.get(0), true);
    }

    @GetMapping("/routes")
    Set<Route> findRoutesNear(@PathVariable float lat, @PathVariable float lng){
        return service.findActiveNear(new Coordinate(lat, lng));
    }

}
