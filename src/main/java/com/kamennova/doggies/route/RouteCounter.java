package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom. DoubleCoordinate;
import com.kamennova.doggies.route.geom.Vector;
import com.kamennova.doggies.user.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RouteCounter {
    private final HashMap<Vector, Set<User>> store;

    public RouteCounter() {
        this.store = new HashMap<>();
    }

    public RouteCounter(List<Route> routes) {
        this.store = new HashMap<>();

        for (final Route route : routes) {
            final List<DoubleCoordinate> coords = route.getFullCoordinates();

            DoubleCoordinate prev = coords.get(0);

            for (int i = 1; i < coords.size(); i++) {
                final DoubleCoordinate curr = coords.get(i);
                Vector vector = formVector(prev, curr);
                this.put(vector, route.getUser());
                prev = curr;
            }
        }
    }

    private Vector formVector(DoubleCoordinate a, DoubleCoordinate b) {
        return a.getLng() < b.getLng() ||
                a.getLng().equals(b.getLng()) && a.getLat() < b.getLat() ? new Vector(a, b) : new Vector(b, a);
    }

    private void put(Vector vector, User user) {
        final Set<User> users = store.getOrDefault(vector, new HashSet<>());
        users.add(user);
        store.put(vector, users);
    }

    private List<CounterResult> merge() {
        final HashMap<Set<User>, ArrayList<Vector>> usersRoutes = new HashMap<>();

        store.forEach((vector, users) -> {
            final ArrayList<Vector> existingRoutes = usersRoutes.getOrDefault(users, new ArrayList<>());
            existingRoutes.add(vector);
            usersRoutes.put(users, existingRoutes);
        });

        return usersRoutes.entrySet().stream()
                .map(entry -> new CounterResult(entry.getKey(), vectorsToRoutes(entry.getValue())))
                .collect(Collectors.toList());
    }

    private Vector getRouteVector(ArrayList<DoubleCoordinate> coords) {
        return new Vector(coords.get(0), coords.get(coords.size() - 1));
    }

    private ArrayList<ArrayList<DoubleCoordinate>> vectorsToRoutes(ArrayList<Vector> vectors) {
        final ArrayList<ArrayList<DoubleCoordinate>> routes = new ArrayList<>();

        for (Vector vector : vectors) {
            final OptionalInt keySearch = IntStream.range(0, routes.size())
                    .filter(i -> vector.isAdjacent(getRouteVector(routes.get(i))))
                    .findFirst();

            if (keySearch.isEmpty()) {
                routes.add(vector.toCoordinates());
                continue;
            }

            final ArrayList<DoubleCoordinate> route = routes.get(keySearch.getAsInt());
            final List<Character> adjacency = vector.getAdjacency(getRouteVector(route));
            final DoubleCoordinate toAdd = adjacency.get(1) == 'b' ? vector.b : vector.a;

            if (adjacency.get(0) == 'a') {
                route.add(0, toAdd);
            } else {
                route.add(toAdd);
            }
        }

        return routes;
    }

    public List<CounterResult> getMap() {
        return this.merge();
    }
}
