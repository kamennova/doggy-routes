package com.kamennova.doggies.route;

import java.util.function.Function;

public class BaseCoordinate<T extends Number> {
    private T lat;
    private T lng;

    BaseCoordinate(T lat, T lng){
        this.lat = lat;
        this.lng = lng;
    }

    public T getLat() {
        return lat;
    }

    public T getLng() {
        return lng;
    }

    public void setLat(T lat) {
        this.lat = lat;
    }

    public void setLng(T lng) {
        this.lng = lng;
    }

    public String toString(){
        return '[' + lat.toString() + "," + lng.toString() + ']';
    }

    public <G extends Number> BaseCoordinate<G> apply(Function<T, G> func){
        return new BaseCoordinate<G>(func.apply(getLat()), func.apply(getLng()));
    }
}

interface CoordinateFunc<G extends Number, T extends Number> {
    BaseCoordinate<G> doSomething(T param);
}