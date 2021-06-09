package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom. DoubleCoordinate;
import com.kamennova.doggies.route.geom.Vector;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RouteCounter {
    private final HashMap<Vector, Set<Long>> store;

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
                this.put(vector, route.getUserId());
                prev = curr;
            }
        }
    }

    private Vector formVector(DoubleCoordinate a, DoubleCoordinate b) {
        return a.getLng() < b.getLng() ||
                a.getLng() == b.getLng() && a.getLat() < b.getLat() ? new Vector(a, b) : new Vector(b, a);
    }

    private void put(Vector vector, Long userId) {
        final Set<Long> idSet = store.getOrDefault(vector, new HashSet<>());
        idSet.add(userId);
        store.put(vector, idSet);
    }

    /*public RouteCounter prune(){
        final short minWalks = getMinWalksLimit();
        final HashMap<Vector, Set<Long>> newStore = store.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() >= minWalks)
                .collect(Collectors.toMap())
                ;
    }*/

    private short getMinWalksLimit() {
        return 1; // todo
    }

    private List<CounterResult> merge() {
        final HashMap<Set<Long>, ArrayList<Vector>> usersRoutes = new HashMap<>();

        store.forEach((vector, userIds) -> {
            final ArrayList<Vector> existingRoutes = usersRoutes.getOrDefault(userIds, new ArrayList<>());
            existingRoutes.add(vector);
            usersRoutes.put(userIds, existingRoutes);
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

    private ArrayList<Double> vectorToCoordinates(Vector vector) {
        return new ArrayList<>(Arrays.asList(vector.a.getLat(), vector.a.getLng(), vector.b.getLat(), vector.b.getLng()));
    }

    public List<CounterResult> getMap() {
        return this.merge();
    }
}
