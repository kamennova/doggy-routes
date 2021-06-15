package com.kamennova.doggies.route.aggregator;

import com.kamennova.doggies.route.Route;
import com.kamennova.doggies.route.geom.Vector;
import com.kamennova.doggies.user.User;

import java.util.*;

/**
 * Returns routes vectors grouped by unique sets of users walking them
 */
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
        // align vectors to same direction to avoid storing 2 identical opposite vectors
        final Vector aligned = vector.alignToEastNorth();
        final Set<User> users = store.getOrDefault(aligned, new HashSet<>());
        users.add(user);
        store.put(aligned, users);
    }

    public Map<Set<User>, ArrayList<Vector>> getMap() {
        final Map<Set<User>, ArrayList<Vector>> usersVectors = new HashMap<>();

        // reverse map (vector -> users) to (users -> vectors)
        store.forEach((vector, users) -> {
            final ArrayList<Vector> existingVectors = usersVectors.getOrDefault(users, new ArrayList<>());
            existingVectors.add(vector);
            usersVectors.put(users, existingVectors);
        });

        return usersVectors;
    }
}
