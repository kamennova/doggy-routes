package com.kamennova.doggies.route;

import com.kamennova.doggies.Helpers;

import java.util.*;
import java.util.stream.Collectors;

public class RouteCounter {
    private final HashMap<Vector, Set<Long>> store;

    public RouteCounter() {
        this.store = new HashMap<>();
    }

    public void put(Vector vector, Long userId) {
        final Set<Long> idSet = store.getOrDefault(vector, new HashSet<>());
        if(idSet.size() > 0){
            System.out.println(idSet);
        }
        idSet.add(userId);
        System.out.println(userId);
        if(idSet.size() > 1){

            System.out.println(userId);
            System.out.println(idSet);
        }
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

    public List<HashMap<String, Object>> merge() {
        final HashMap<Set<Long>, ArrayList<Vector>> grouped = new HashMap<>();

        store.entrySet().forEach(entry -> {
            final ArrayList<Vector> upd = grouped.getOrDefault(entry.getValue(), new ArrayList<>());
            upd.add(entry.getKey());
            grouped.put(entry.getValue(), upd);
        });

        return grouped.entrySet().stream().map(entry -> {
            final HashMap<String, Object> res = new HashMap<>();
            res.put("route", entry.getValue());
            res.put("ids", entry.getKey());

            return res;
        }).collect(Collectors.toList());
    }
}
