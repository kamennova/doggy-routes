package com.kamennova.doggies.route.geom;

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

    public boolean isInside(DoubleCoordinate coord) {
        return bottom <= coord.getLat() &&
                coord.getLat() <= top &&
                left <= coord.getLng() &&
                coord.getLng() <= right;
    }
}
