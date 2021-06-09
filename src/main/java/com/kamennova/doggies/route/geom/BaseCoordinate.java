package com.kamennova.doggies.route;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseCoordinate<?> that = (BaseCoordinate<?>) o;
        return Objects.equals(lat, that.lat) &&
                Objects.equals(lng, that.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }
}
