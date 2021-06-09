package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.DoubleCoordinate;

import java.util.ArrayList;
import java.util.Set;

public class CounterResult {
    public Set<Long> userIds;
    public ArrayList<ArrayList<DoubleCoordinate>> routes;

    CounterResult(Set<Long> ids, ArrayList<ArrayList<DoubleCoordinate>> coords){
        this.userIds = ids;
        this.routes = coords;
    }
}
