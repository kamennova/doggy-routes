package com.kamennova.doggies.route.response;

import java.util.List;
import java.util.Set;

public class RouteMap {
    public Set<Long> dogIds;
    public List<List<Double[]>> routes;

    public RouteMap(Set<Long> dogIds, List<List<Double[]>> routes){
        this.dogIds = dogIds;
        this.routes = routes;
    }
}
