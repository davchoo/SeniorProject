package team.travel.travelplanner.entity;

import jakarta.persistence.*;

@Entity
public class GasStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //Since we can't save lat lng, maybe we can save address and reverse geocode?
    private String address;

    //Probably cant use
    private double latitude;
    private double longitude;
    private double pricePerGallon;

    public GasStation() {
    }

    public GasStation(String name, double latitude, double longitude, double pricePerGallon) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pricePerGallon = pricePerGallon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPricePerGallon() {
        return pricePerGallon;
    }

    public void setPricePerGallon(double pricePerGallon) {
        this.pricePerGallon = pricePerGallon;
    }
}
