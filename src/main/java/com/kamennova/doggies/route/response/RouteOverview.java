package com.kamennova.doggies.route.response;

import java.util.List;

public class RouteOverview {
    public Long id;
    public int length;
    public List<Double[]> coordinates;

    public RouteOverview(Long id, int length, List<Double[]> coords){
        this.id = id;
        this.length = length;
        this.coordinates = coords;
    }
}
