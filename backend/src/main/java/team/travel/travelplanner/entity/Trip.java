package team.travel.travelplanner.entity;

import com.google.maps.model.DirectionsResult;
import jakarta.persistence.*;

@Entity
public abstract class Trip {
    @Id
    @GeneratedValue
    private Long id;

    private String origin;

    private String destination;

    @Column(length = 65535, columnDefinition = "TEXT")
    private String encodedPolyline;

    @Transient
    private DirectionsResult directionsResult;

    public Trip(String origin, String destination, DirectionsResult directionsResult) {
        this.origin = origin;
        this.destination = destination;
        this.directionsResult = directionsResult;
        this.encodedPolyline = directionsResult.routes[0].overviewPolyline.getEncodedPath();
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

    public DirectionsResult getDirectionsResult() {
        return directionsResult;
    }

    public void setDirectionsResult(DirectionsResult directionsResult) {
        this.directionsResult = directionsResult;
    }

    public String getEncodedPolyline(){
        return encodedPolyline;
    }

    public void setEncodedPolyline(String encodedPolyline){
        this.encodedPolyline = encodedPolyline;
    }
}
