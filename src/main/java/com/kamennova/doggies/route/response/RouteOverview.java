package com.kamennova.doggies.route.response;

import com.kamennova.doggies.dog.response.DogOverview;

import java.util.List;

public class RouteOverview {
    public Double[] coordinate;
    public List<DogOverview> dogs;

    public RouteOverview(Double[] c, List<DogOverview> dogs){
        this.dogs = dogs;
        this.coordinate = c;
    }
}
