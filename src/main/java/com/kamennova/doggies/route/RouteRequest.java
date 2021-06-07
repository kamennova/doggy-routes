package com.kamennova.doggies.route;

import java.util.List;

public class RouteRequest {
    public List<Double> coordinates;

    RouteRequest(){}

    RouteRequest(List<Double> coords){
        this.coordinates = coords;
    }
}
