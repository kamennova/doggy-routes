package com.kamennova.doggies.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {
    @Autowired
    RouteRepository repository;

    public static final Boundary Kyiv = new Boundary(30.175930, 50.588778, 30.865690, 50.280173);

    public List<HashMap<String, Object>> getRoutesInfoOfUser(Long userId) {
        return repository.findByUserId(userId).stream().map(route -> {
            final HashMap<String, Object> obj = new HashMap<>();
            obj.put("id", route.getId());
            obj.put("length", route.getLength());
            obj.put("coords", route.getFullCoordinates()
                    .stream().map(c -> new Double[]{c.getLng(), c.getLat()}).collect(Collectors.toList()));

            return obj;
        }).collect(Collectors.toList());
    }

    /**
     * 1. Fetch all active routes with start point in 30-km radius from coord
     * 2.
     */
    public List<HashMap<String, Object>> findActiveNear(Coordinate coord) {
        final List<Route> routes = findWithFilter(coord);
        final RouteCounter vectorsWeights = getVectorsWithDogs(routes);

        return vectorsWeights.merge();
    }

    private List<Route> findWithFilter(Coordinate coord) {
        final List<Route> routes = repository.findAll();

        return routes;
    }

    private RouteCounter getVectorsWithDogs(List<Route> routes) {
        final RouteCounter routeCounter = new RouteCounter();

        for (final Route curr : routes) {
            final List<Coordinate> coords = curr.getFullCoordinates();

            Coordinate prev = coords.get(0);

            for (int i = 1; i < coords.size(); i++) {
                Vector vector = formVector(prev, coords.get(i));
                routeCounter.put(vector, curr.getUserId());
            }
        }

        return routeCounter;
    }

    private Vector formVector(Coordinate a, Coordinate b) {
        return a.getLng() < b.getLng() ||
                a.getLng() == b.getLng() && a.getLat() < b.getLat() ? new Vector(a, b) : new Vector(b, a);
    }

    public List<Coordinate> foldToCoordinates(List<Double> arr) {
        final List<Coordinate> result = new ArrayList<>();

        for (int i = 0; i < arr.size() / 2; i++) {
            result.add(new Coordinate(arr.get(i * 2), arr.get(i * 2 + 1)));
        }

        return result;
    }

    public String validateCoordinates(List<Coordinate> coords) {
        if (coords.size() < 2) {
            return "Маршрут занадто короткий";
        } else if (!this.isInKyiv(coords)) {
            return "Маршрут виходить за межі Києва";
        } else if (coords.size() > 5000) {
            return "Маршрут занадто складний";
        } else if (!this.isLengthValid(coords)) {
            return "Маршрут занадто довгий";
        }

        return "";
    }

    private boolean isLengthValid(List<Coordinate> coords) {
        Coordinate prev = coords.get(0);
        double length = 0;

        for (int i = 1; i < coords.size(); i++) {
            final Coordinate curr = coords.get(i);
            length += Math.sqrt(Math.pow(curr.getLat() - prev.getLat(), 2) + Math.pow(curr.getLng() - prev.getLng(), 2));
        }

        // Max length of route is 70km == 1 degree in Kyiv (50) latitude
        return length < 1;
    }

    private boolean isInKyiv(List<Coordinate> coords) {
        return coords.stream().allMatch(Kyiv::isInside);
    }
}
