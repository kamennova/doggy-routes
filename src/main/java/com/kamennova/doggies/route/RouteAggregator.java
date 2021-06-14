package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.Vector;
import com.kamennova.doggies.user.User;

import java.util.*;
import java.util.stream.Collectors;

public class RouteAggregator {
    private final HashMap<Vector, Set<User>> store;

    public RouteAggregator(List<Route> routes) {
        this.store = new HashMap<>();
        this.putRoutes(routes);
    }

    public void putRoutes(List<Route> routes) {
        for (final Route route : routes) {
            final User user = route.getUser();
            route.getVectors().forEach(v -> put(v, user));
        }
    }

    private void put(Vector vector, User user) {
        final Set<User> users = store.getOrDefault(vector.alignToEastNorth(), new HashSet<>());
        users.add(user);
        store.put(vector, users);
    }

    public List<RouteGroup> getMap() {
        final Map<Set<User>, ArrayList<Vector>> usersVectors = new HashMap<>();

        // reverse map (vector -> users) to (users -> vectors)
        store.forEach((vector, users) -> {
            final ArrayList<Vector> existingVectors = usersVectors.getOrDefault(users, new ArrayList<>());
            existingVectors.add(vector);
            usersVectors.put(users, existingVectors);
        });

        return usersVectors.entrySet().stream()
                .map(entry -> new RouteGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
