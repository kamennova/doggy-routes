package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.route.geom.Vector;
import com.kamennova.doggies.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.IntStream;

public class RouteGroup {
    public Set<User> users;
    public ArrayList<Coordinates> routes;

    RouteGroup(Set<User> users, ArrayList<Vector> vectors) {
        this.users = users;
        this.routes = group(vectors);
    }

    private ArrayList<Coordinates> group(List<Vector> vectors) {
        final ArrayList<Coordinates> startGroups = formStartGroups(vectors);
        // todo connect
        return startGroups;
    }

    private ArrayList<Coordinates> formStartGroups(List<Vector> vectors) {
        final ArrayList<Coordinates> routes = new ArrayList<>();

        for (Vector vector : vectors) {
            final OptionalInt keySearch = IntStream.range(0, routes.size())
                    .filter(i -> vector.isAdjacent(routes.get(i).getDirection())).findFirst();

            if (keySearch.isEmpty()) {
                routes.add(vector.toCoordinates());
                continue;
            }

            final Coordinates route = routes.get(keySearch.getAsInt());
            final List<Character> adjacency = vector.getAdjacency(route.getDirection());
            // get non-coinciding coordinate of vector
            final DoubleCoordinate toAdd = adjacency.get(0) == 'b' ? vector.a : vector.b;

            if (adjacency.get(1) == 'a') { // adjacent to start of route
                route.prepend(toAdd);
            } else {
                route.append(toAdd);
            }
        }

        return routes;
    }
}
