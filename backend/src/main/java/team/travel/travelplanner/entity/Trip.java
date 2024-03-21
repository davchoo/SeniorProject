package team.travel.travelplanner.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.locationtech.jts.geom.LineString;

@Entity
public abstract class Trip {
    @Id
    @GeneratedValue
    private Long id;

    private String origin;

    private String destination;

    private LineString lineString;

    public Trip(String origin, String destination, LineString linestring) {
        this.origin = origin;
        this.destination = destination;
        this.lineString = linestring;
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
}
