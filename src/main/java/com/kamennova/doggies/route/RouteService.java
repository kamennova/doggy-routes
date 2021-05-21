package com.kamennova.doggies.route;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class RouteService {
    RouteRepository repository;

    RouteService(RouteRepository repository){
        this.repository = repository;
    }

    /**
     * 1. Fetch all active routes with start point in 30-km radius from coord
     * 2.
     */
    public Set<Route> findActiveNear(Coordinate coord){
        final List<Route> routes = repository.findAll();
        System.out.print(routes.size());

        return Collections.emptySet();
    }
}
