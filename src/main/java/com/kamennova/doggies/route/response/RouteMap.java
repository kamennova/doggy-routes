package com.kamennova.doggies.route.response;

import java.util.List;
import java.util.Set;

public class RouteMap {
    public Set<Long> dogs;
    public List<List<Double[]>> routes;

    public RouteMap(Set<Long> dogIds, List<List<Double[]>> routes){
        this.dogs = dogIds;
        this.routes = routes;
    }
}
