package com.kamennova.doggies.route.geom;

/**
 * Rectangle boundary with left and right representing min and max longitude,
 * bottom and top - min & max latitude
 */
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

    public boolean contains(DoubleCoordinate coord) {
        return bottom <= coord.getLat() &&
                coord.getLat() <= top &&
                left <= coord.getLng() &&
                coord.getLng() <= right;
    }
}
