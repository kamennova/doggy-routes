package com.kamennova.doggies.route;

import java.util.List;

public class RouteRequest {
    public List<Coordinate> coords;
    public Long userId;

    RouteRequest(List<Coordinate> coords, Long userId){
        this.coords = coords;
        this.userId = userId;
    }
}
