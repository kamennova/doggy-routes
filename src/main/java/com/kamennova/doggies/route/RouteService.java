package com.kamennova.doggies.route;

import com.kamennova.doggies.dog.Dog;
import com.kamennova.doggies.dog.response.DogOverview;
import com.kamennova.doggies.route.aggregator.RouteAggregator;
import com.kamennova.doggies.route.aggregator.RouteGroup;
import com.kamennova.doggies.route.geom.Boundary;
import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.route.geom.Vector;
import com.kamennova.doggies.route.response.PublicRouteOverview;
import com.kamennova.doggies.route.response.RouteMap;
import com.kamennova.doggies.route.response.RouteOverview;
import com.kamennova.doggies.user.User;
import com.kamennova.doggies.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteService {
    @Autowired
    RouteRepository repository;

    @Autowired
    UserRepository userRepository;

    public static final Boundary Kyiv = new Boundary(30.175930, 50.588778, 30.865690, 50.280173);

    public List<RouteOverview> getRoutesInfoOfUser(Long userId) {
        return userRepository.getOne(userId).getRoutes().stream()
                .map(route -> new RouteOverview(
                        route.getId(),
                        route.getLength(),
                        transformCoordinates(route.getFullCoordinates())))
                .sorted(Comparator.comparing(o -> o.id))
                .collect(Collectors.toList());
    }

    /**
     * Returns every route's median point coordinate and dogs
     */
    public List<PublicRouteOverview> getMapOverview() {
        return repository.findAll().stream()
                .map(route -> new PublicRouteOverview(
                        transformCoordinate(route.getMedian()),
                        route.getUser().getDogs().stream().map(Dog::getOverview).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    /**
     * Returns groups of vectors with corresponding dogs ids
     */
    public List<RouteMap> getDetailedMap(List<Route> routes) {
        final Map<Set<User>, ArrayList<Vector>> userVectors = new RouteAggregator(routes).getMap();

        return userVectors.entrySet().stream().map(entry -> new RouteMap(
                entry.getKey().stream().flatMap(u -> u.getDogs().stream().map(Dog::getId)).collect(Collectors.toSet()),
                RouteGroup.group(entry.getValue()).stream().map(r -> transformCoordinates(r.get())).collect(Collectors.toList())) // todo?
        ).collect(Collectors.toList());
    }

    public Set<DogOverview> getRoutesDogs(List<Route> routes) {
        final Set<User> users = routes.stream().map(Route::getUser).collect(Collectors.toSet());
        return users.stream().flatMap(u -> u.getDogs().stream())
                .map(Dog::getOverview).collect(Collectors.toSet());
    }

    /**
     * Finds all routes in 30km radius of coordinate
     */
    public List<Route> findWithFilter(DoubleCoordinate coordinate) { // todo
        final List<Route> routes = repository.findAll();

        return routes;
    }

    /**
     * Transforms 1-dimensional array of Doubles into DoubleCoordinates array
     */
    public List<DoubleCoordinate> foldToCoordinates(List<Double> arr) {
        final List<DoubleCoordinate> result = new ArrayList<>();

        for (int i = 0; i < arr.size() / 2; i++) {
            result.add(new DoubleCoordinate(arr.get(i * 2), arr.get(i * 2 + 1)));
        }

        return result;
    }

    public String validateCoordinates(List<DoubleCoordinate> coords, Integer length) {
        if (coords.size() < 2) {
            return "Маршрут занадто короткий";
        } else if (!coords.stream().allMatch(RouteService::isInKyiv)) {
            return "Маршрут виходить за межі Києва";
        } else if (coords.size() > 5000) {
            return "Маршрут занадто складний";
        } else if (length > 70000) {
            return "Маршрут занадто довгий";
        }

        return "";
    }

    public Integer getRouteLength(List<DoubleCoordinate> coords) {
        DoubleCoordinate prev = coords.get(0);
        double length = 0;

        for (int i = 1; i < coords.size(); i++) {
            final DoubleCoordinate curr = coords.get(i);
            length += Math.sqrt(Math.pow(curr.getLat() - prev.getLat(), 2) + Math.pow(curr.getLng() - prev.getLng(), 2));
        }

        return (int) (length * 70 * 100);
    }

    public static boolean isInKyiv(DoubleCoordinate coordinate) {
        return Kyiv.contains(coordinate);
    }

    private List<Double[]> transformCoordinates(List<DoubleCoordinate> coords) {
        return coords.stream().map(RouteService::transformCoordinate).collect(Collectors.toList());
    }

    /**
     * Transforms DoubleCoordinate into array of doubles of OpenLayers format ([lng, lat])
     */
    public static Double[] transformCoordinate(DoubleCoordinate c) {
        return new Double[]{c.getLng(), c.getLat()};
    }
}
