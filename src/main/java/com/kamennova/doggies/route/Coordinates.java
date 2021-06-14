package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.route.geom.Vector;

import java.util.ArrayList;

public class Coordinates {
    private ArrayList<DoubleCoordinate> coords;

    public Coordinates(ArrayList<DoubleCoordinate> c) {
        this.coords = c;
    }

    public void append(DoubleCoordinate c){
        this.coords.add(c);
    }

    public void prepend(DoubleCoordinate c){
        this.coords.add(0, c);
    }

    public ArrayList<DoubleCoordinate> get(){
        return coords;
    }

    public Vector getDirection() {
        return new Vector(coords.get(0), coords.get(coords.size() - 1));
    }
}
