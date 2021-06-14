package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.user.User;

import java.util.ArrayList;
import java.util.Set;

public class CounterResult {
    public Set<User> users;
    public ArrayList<ArrayList<DoubleCoordinate>> routes;

    CounterResult(Set<User> users, ArrayList<ArrayList<DoubleCoordinate>> coords){
        this.users = users;
        this.routes = coords;
    }
}
