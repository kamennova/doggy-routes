package com.kamennova.doggies.route;

public class Vector {
    public Coordinate a;
    public Coordinate b;
    private Double length;

    Vector(Coordinate a, Coordinate b) {
        this.a = a;
        this.b = b;
    }

    private void calcLength() {
        this.length = Math.sqrt(Math.pow(a.getLat() - b.getLat(), 2) + Math.pow(a.getLng() - b.getLng(), 2));
    }

    public Double getLength() {
        if (length == null) {
            this.calcLength();
        }

        return length;
    }
}
