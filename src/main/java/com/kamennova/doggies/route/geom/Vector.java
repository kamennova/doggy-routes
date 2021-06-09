package com.kamennova.doggies.route;

import java.util.*;

public class Vector {
    public DoubleCoordinate a;
    public DoubleCoordinate b;
    private Double length;

    public Vector(DoubleCoordinate a, DoubleCoordinate b) {
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

    public String toString() {
        return "[" + a.toString() + ", " + b.toString() + "]";
    }

    public List<Character> getAdjacency(Vector vector) {
        if (a.equals(vector.a)) {
            return List.of('a', 'a');
        } else if (a.equals(vector.b)) {
            return List.of('a', 'b');
        } else if (b.equals(vector.a)) {
            return List.of('b', 'a');
        } else if (b.equals(vector.b)) {
            return List.of('b', 'b');
        }

        return Collections.emptyList();
    }

    public boolean isAdjacent(Vector vector) {
        return getAdjacency(vector).size() > 0;
    }

    public Vector reverse() {
        return new Vector(b, a);
    }

    public ArrayList<DoubleCoordinate> toCoordinates() {
        return new ArrayList<>(Arrays.asList(a, b));
    }
}
