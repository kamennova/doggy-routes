package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.BaseCoordinate;
import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.route.geom.IntCoordinate;
import com.kamennova.doggies.route.geom.Vector;
import com.kamennova.doggies.user.User;

import javax.persistence.*;
import java.util.ArrayList;
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
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "start_id", referencedColumnName = "id", nullable = false)
    private CoordinateEntity start;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "median_id", referencedColumnName = "id", nullable = false)
    private CoordinateEntity median;

    @Column(name = "coordinates")
    private String compressedCoordinates;

    private boolean isActive;

    private Integer length;

    Route(List<DoubleCoordinate> coords, User user, Integer length) {
        this.user = user;
        this.isActive = true;
        this.length = length;
        this.compressedCoordinates = CoordinatesCoder.encode(coords);

        final DoubleCoordinate start = coords.get(0);
        this.start = new CoordinateEntity(start.getLat(), start.getLng());

        final DoubleCoordinate median = coords.get(coords.size() / 2);
        this.median = new CoordinateEntity(median.getLat(), median.getLng());
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

    public DoubleCoordinate getMedian() {
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
    }

    public void setMedian(CoordinateEntity c) {
        this.median = c;
    }

    // todo move to coordinates?
    public List<Vector> getVectors() {
        final List<DoubleCoordinate> coordinates = getFullCoordinates();
        final List<Vector> vectors = new ArrayList<>();

        DoubleCoordinate prev = coordinates.get(0);

        for (int i = 1; i < coordinates.size(); i++) {
            final DoubleCoordinate curr = coordinates.get(i);
            vectors.add(new Vector(prev, curr));
            prev = curr;
        }

        return vectors;
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
        return "Route{" + "id=" + this.id + ", start='" + this.start + "'}";
    }
}
