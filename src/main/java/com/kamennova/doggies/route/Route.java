package com.kamennova.doggies.route;

import com.kamennova.doggies.user.User;
import com.sun.istack.NotNull;

import javax.persistence.*;
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
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @NotNull
    private User user;

    @Column(name="user_id", nullable  = false)
    private Long userId;

    private Double startLat;
    private Double startLng;

    @Column(name = "coordinates")
    private String compressedCoordinates;

    private boolean isActive;
    private Integer length;

    Route(List<Coordinate> coords, User user, boolean isActive, Integer length) {
        this.user = user;
        this.isActive = isActive;
        this.length = length;
        this.startLat = coords.get(0).getLat();
        this.startLng = coords.get(0).getLng();
        this.compressedCoordinates = CoordinatesCoder.encode(coords);
        System.out.println(compressedCoordinates);
    }

    public Long getId() {
        return this.id;
    }

    public Long getUserId() {
        return this.userId;
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

    public List<Coordinate> getFullCoordinates() {
        return CoordinatesCoder.decode(this.compressedCoordinates, this.getStart());
    }

    public Coordinate getStart() {
        return new Coordinate(startLat, startLng);
    }

    public String getCompressedCoordinates() {
        return this.compressedCoordinates;
    }

    public void setCompressedCoordinates(String encoded) {
        this.compressedCoordinates = encoded;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Route))
            return false;
        Route route = (Route) o;
        return Objects.equals(this.id, route.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.startLat);
    }

    @Override
    public String toString() {
        return "Route{" + "id=" + this.id + ", polyline='" + this.startLng + "'}";
    }
}
