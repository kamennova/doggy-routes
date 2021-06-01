package com.kamennova.doggies.route;

import java.util.List;

public class RouteRequest {
    public List<Double> coords;

    RouteRequest(List<Double> coords){
        this.coords = coords;
    }
}
