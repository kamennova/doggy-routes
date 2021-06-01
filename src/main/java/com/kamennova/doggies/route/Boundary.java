package com.kamennova.doggies.route;

public class Boundary {
    double left;
    double right;
    double top;
    double bottom;

    public Boundary(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public boolean isInside(Coordinate coord) {
        return bottom <= coord.getLat() &&
                coord.getLat() <= top &&
                left <= coord.getLng() &&
                coord.getLng() <= right;
    }
}