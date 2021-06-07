package com.kamennova.doggies.route;

import java.util.Objects;

public class Vector {
    public Coordinate a;
    public Coordinate b;
    private Double length;

    public Vector(Coordinate a, Coordinate b) {
        this.a = a;
        this.b = b;
    }

    private void calcLength() {
        this.length = Math.sqrt(Math.pow(a.getLat() - b.getLat(), 2) + Math.pow(a.getLng() - b.getLng(), 2));
    }

    public Double setLength() {
        if (length == null) {
            this.calcLength();
        }

        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return Objects.equals(a, vector.a) && Objects.equals(b, vector.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
