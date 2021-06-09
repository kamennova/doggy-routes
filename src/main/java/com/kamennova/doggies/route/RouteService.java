package com.kamennova.doggies.route;

import com.kamennova.doggies.dog.Dog;
import com.kamennova.doggies.route.geom.Boundary;
import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.route.response.RouteOverview;
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
            obj.put("coords", transformCoordinates(route.getFullCoordinates()));

            return obj;
        }).collect(Collectors.toList());
    }

    /**
     * Returns median point coordinates with dogs of all routes.
     */
    public List<RouteOverview> getRoutesOverview() {
        return repository.findAll().stream()
                .map(route -> new RouteOverview(
                        transformCoordinate(route.getMedian()),
                        route.getUser().getDogs().stream().map(Dog::getOverview).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    /**
     * Fetches routes in set radius of coordinate & merges dogs info on each route vector
     */
    public List<HashMap<String, Object>> findRoutesNear(DoubleCoordinate coord) {
        final List<Route> routes = findWithFilter(coord);
        final RouteCounter routeCounter = new RouteCounter(routes);

        return routeCounter.getMap().stream().map(obj -> {
            final HashMap<String, Object> res = new HashMap<>();
            res.put("routes", obj.routes.stream().map(this::transformCoordinates).collect(Collectors.toList()));
            res.put("userIds", obj.userIds);
            return res;
        }).collect(Collectors.toList());
    }

    private List<Route> findWithFilter(DoubleCoordinate coord) { // todo
        final List<Route> routes = repository.findAll();

        return routes;
    }

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
        } else if (!this.isInKyiv(coords)) {
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

        System.out.println(length);
        return (int) (length * 70 * 1000);
    }

    private boolean isInKyiv(List<DoubleCoordinate> coords) {
        return coords.stream().allMatch(Kyiv::isInside);
    }

    private List<Double[]> transformCoordinates(List<DoubleCoordinate> coords) {
        return coords.stream().map(RouteService::transformCoordinate).collect(Collectors.toList());
    }

    public static Double[] transformCoordinate(DoubleCoordinate c) {
        return new Double[]{c.getLng(), c.getLat()};
    }
}
