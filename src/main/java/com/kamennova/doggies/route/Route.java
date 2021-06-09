package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.BaseCoordinate;
import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.route.geom.IntCoordinate;
import com.kamennova.doggies.user.User;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToOne
    @JoinColumn(name="start_id", referencedColumnName = "id", nullable = false)
    private Coordinate start;

    @OneToOne
    @JoinColumn(name="median_id", referencedColumnName = "id", nullable = false)
    private Coordinate median;

    @Column(name = "coordinates")
    private String compressedCoordinates;

    private boolean isActive;

    private Integer length;

    Route(List<DoubleCoordinate> coords, User user, boolean isActive, Integer length) {
        this.user = user;
        this.isActive = isActive;
        this.length = length;
        final DoubleCoordinate start = coords.get(0);
        this.start = new Coordinate(start.getLat(), start.getLng());
        this.compressedCoordinates = CoordinatesCoder.encode(coords);
        this.userId = user.getId();
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

    public List<IntCoordinate> getDecodedReducedCoordinates() {
        return CoordinatesCoder.decode(this.compressedCoordinates, this.getStart());
    }

    public List<DoubleCoordinate> getFullCoordinates() {
        return CoordinatesCoder.decode(this.compressedCoordinates, this.getStart())
                .stream().map(c -> {
                    final BaseCoordinate<Double> f = c.apply(CoordinatesCoder::getFullFromMinAndSec);
                    return new DoubleCoordinate(50 + f.getLat(), 30 + f.getLng());
                })
                .collect(Collectors.toList());
    }

    public DoubleCoordinate getStart() {
        return new DoubleCoordinate(start.getLat(), start.getLng());
    }

    public DoubleCoordinate getMedian(){
        return new DoubleCoordinate(median.getLat(), median.getLng());
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

    public void setMedian(Coordinate c) {
        this.median = c;
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
        return Objects.hash(this.id, this.start);
    }

    @Override
    public String toString() {
        return "Route{" + "id=" + this.id + ", polyline='" + this.start + "'}";
    }
}
