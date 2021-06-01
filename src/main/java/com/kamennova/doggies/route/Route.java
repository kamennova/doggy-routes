package com.kamennova.doggies.route;

import com.kamennova.doggies.user.User;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "routes")
public class Route {
    Route() {
    }

    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @NotNull
    private User user;

    @Transient
    private List<Integer> coordinates;
    private boolean isActive;
    private String polylineStr;
    private Integer length;

    Route(String polylineStr, Coordinate start, boolean isActive) {
        this.polylineStr = polylineStr;
        this.isActive = isActive;
    }

    Route(List<Coordinate> coords, boolean isActive) {
        this.isActive = isActive;
    }

    public Long getId() {
        return this.id;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public User getUser() {
        return this.user;
    }

    public String getPolylineStr() {
        return this.polylineStr;
    }

    public void setPolylineStr(String str) {
        this.polylineStr = str;
    }

    public List<Coordinate> getFullCoordinates(){
//        return this.coordinates;
        return Collections.emptyList();
    }

    public List<Coordinate> getReducedCoordinates() {
        return new ArrayList<Coordinate>();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Route))
            return false;
        Route route = (Route) o;
        return Objects.equals(this.id, route.id) && Objects.equals(this.polylineStr, route.polylineStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.polylineStr);
    }

    @Override
    public String toString() {
        return "Route{" + "id=" + this.id + ", polyline='" + this.polylineStr + "'}";
    }
}
