package team.travel.travelplanner.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class GasStation {
    @Id
    private String id;
    private String name;

    @Embedded
    private DisplayName displayName;
    private String formattedAddress;

    private String googleMapsUri;

    @OneToMany(
            mappedBy = "gasStation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Reviews> reviews;

    @OneToOne(cascade = CascadeType.ALL)
    private FuelOptions fuelOptions;

    @Embedded
    private CurrentOpeningHours currentOpeningHours;

    private double rating;

    @Transient
    private Location location;

    public GasStation() {
    }

    public GasStation(String name, String placeId, String formattedAddress, String googleMapsUri,
                      List<Reviews> reviews, FuelOptions fuelOptions,
                      CurrentOpeningHours currentOpeningHours, double rating, Location location) {
        this.name = name;
        this.id = placeId;
        this.formattedAddress = formattedAddress;
        this.googleMapsUri = googleMapsUri;
        this.reviews = reviews;
        this.fuelOptions = fuelOptions;
        this.currentOpeningHours = currentOpeningHours;
        this.rating = rating;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return id;
    }

    public void setPlaceId(String id) {
        this.id = id;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }


    public String getGoogleMapsUri() {
        return googleMapsUri;
    }

    public void setGoogleMapsUri(String googleMapsUri) {
        this.googleMapsUri = googleMapsUri;
    }

    public List<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<Reviews> reviews) {
        this.reviews = reviews;
    }

    public FuelOptions getFuelOptions() {
        return fuelOptions;
    }

    public void setFuelOptions(FuelOptions fuelOptions) {
        this.fuelOptions = fuelOptions;
    }

    public CurrentOpeningHours getCurrentOpeningHours() {
        return currentOpeningHours;
    }

    public void setCurrentOpeningHours(CurrentOpeningHours currentOpeningHours) {
        this.currentOpeningHours = currentOpeningHours;
    }

    public double getRating(){
        return this.rating;
    }

    public Location getLocation(){
        return location;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public void setDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(Reviews review : reviews){
            s.append(review.toString()).append("\n");
        }
        return "GasStation{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                ", googleMapsUri='" + googleMapsUri + '\'' +
                ", reviews=" + s.toString() +
                ", fuelOptions=" + fuelOptions.toString() +
                ", currentOpeningHours=" + ((currentOpeningHours!=null) ? currentOpeningHours.toString() : " no data") +
                ", rating=" + rating +
                '}';
    }

    public static class CurrentOpeningHours {
        @JsonProperty("weekdayDescriptions")
        private List<String> weekdayDescriptions;
        @Override
        public String toString() {

            StringBuilder description = new StringBuilder();
            for(String s : weekdayDescriptions){
                description.append(s).append("\n");
            }
            return "CurrentOpeningHours{" +
                    "weekdayDescriptions=" + description.toString() +
                    '}';
        }

        public void setWeekdayDescriptions(List<String> weekdayDescriptions){
            this.weekdayDescriptions = weekdayDescriptions;
        }
    }

    public static class DisplayName{
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Location {
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

        private double latitude;
        private double longitude;

    }
}


