package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.DoubleCoordinate;

import javax.persistence.*;

@Entity
@Table(name = "coordinates")
public class CoordinateEntity extends DoubleCoordinate {
    public CoordinateEntity() {
        super();
    }

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Double lat;
    private Double lng;

    public CoordinateEntity(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
