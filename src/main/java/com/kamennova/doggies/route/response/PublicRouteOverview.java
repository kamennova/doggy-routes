package com.kamennova.doggies.route.response;

import java.util.List;

public class PublicRouteOverview {
    public Double[] coordinate;
    public List<Long> dogs;

    public PublicRouteOverview(Double[] c, List<Long> dogs){
        this.dogs = dogs;
        this.coordinate = c;
    }
}
