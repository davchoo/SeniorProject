package team.travel.travelplanner.entity;

import jakarta.persistence.*;
import org.locationtech.jts.geom.LineString;

@Entity
public abstract class Trip {
    @Id
    @GeneratedValue
    private Long id;

    private String origin;

    private String destination;

    private LineString lineString;

    @ManyToOne
    private User user;

    public Trip(String origin, String destination, LineString linestring, User user) {
        this.origin = origin;
        this.destination = destination;
        this.lineString = linestring;
        this.user = user;
    }

    public Trip() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LineString getLineString() {
        return lineString;
    }

    public void setLineString(LineString lineString) {
        this.lineString = lineString;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }
}
