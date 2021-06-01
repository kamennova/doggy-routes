package com.kamennova.doggies.route;

import com.kamennova.doggies.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteService {
    @Autowired
    RouteRepository repository;
    public static final Boundary Kyiv = new Boundary(30.175930, 50.588778, 30.865690, 50.280173);

    public List<HashMap<String, String>> getRoutesInfoOfUser(Long userId){
        return repository.findByUserId(userId).stream().map(route -> {
            final HashMap<String, String> obj = new HashMap<>();
            obj.put("length", route.getLength().toString());
            obj.put("coords", route.getFullCoordinates().toString());

            return obj;
        }).collect(Collectors.toList());
    }

    /**
     * 1. Fetch all active routes with start point in 30-km radius from coord
     * 2.
     */
    public Set<Route> findActiveNear(Coordinate coord) {
        final List<Route> routes = findWithFilter(coord);
        final Map<Vector, List<Short>> vectorsWeights = prune(getVectorsWithDogs(routes));

        return Collections.emptySet();
    }

    private List<Route> findWithFilter(Coordinate coord) {
        final List<Route> routes = repository.findAll();
        System.out.print(routes.size());

        return routes;
    }

    private HashMap<Vector, List<Short>> getVectorsWithDogs(List<Route> routes) {
        final HashMap<Vector, List<Short>> routeCounter = new HashMap<>();

        for (final Route curr : routes) {
            final List<Coordinate> coords = curr.getReducedCoordinates();
            final List<Short> breedIds = curr.getUser().getDogs().stream().map(dog -> dog.getBreed().getId()).collect(Collectors.toList());

            Coordinate prev = coords.get(0);
            for (int i = 1; i < coords.size(); i++) {
                Vector vector = formVector(prev, coords.get(i));
                final List<Short> breeds = Helpers.concatLists(routeCounter.getOrDefault(vector, Collections.emptyList()), breedIds);
                routeCounter.put(vector, breeds);
            }
        }

        return routeCounter;
    }

    private Vector formVector(Coordinate a, Coordinate b) {
        return a.getLng() < b.getLng() ||
                a.getLng() == b.getLng() && a.getLat() < b.getLat() ? new Vector(a, b) : new Vector(b, a);
    }

    private Map<Vector, List<Short>> prune(HashMap<Vector, List<Short>> vectors){
        final short minWalks = getMinWalksLimit(vectors);
        return vectors.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() >= minWalks)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private short getMinWalksLimit(HashMap<Vector, List<Short>> vectors){
        return 2;
    }

    public List<Coordinate> foldToCoordinates(List<Double> arr) {
        final List<Coordinate> result = new ArrayList<>();

        for (int i = 0; i < arr.size(); i++) {
            if (i > 0 && i % 2 == 0) {
                result.add(new Coordinate(arr.get(i - 1), arr.get(i)));
            }
        }

        return result;
    }

    public String validateCoordinates(List<Coordinate> coords) {
        if (coords.size() < 2) {
            return "Маршрут занадто короткий";
        } else if (!this.isInKyiv(coords)) {
            return "Маршрут виходить за межі Києва";
        } else if (this.isLengthValid(coords)) {
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

        System.out.println(length);
        return length < 2;
    }

    private boolean isInKyiv(List<Coordinate> coords) {
        return coords.stream().allMatch(Kyiv::isInside);
    }
}
