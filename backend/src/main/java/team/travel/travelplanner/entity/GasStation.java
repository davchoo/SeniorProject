package team.travel.travelplanner.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;


@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class GasStation {

    @Id
    private String id;
    private String name;
    private String formattedAddress;

    private String googleMapsUri;

    @OneToMany
    private List<Reviews> reviews;

    @OneToOne
    private FuelOptions fuelOptions;

    @Embedded
    private CurrentOpeningHours currentOpeningHours;

    private double rating;

    public GasStation() {
    }

    public GasStation(String name, String placeId, String formattedAddress, String googleMapsUri,
                      List<Reviews> reviews, FuelOptions fuelOptions,
                      CurrentOpeningHours currentOpeningHours, double rating) {
        this.name = name;
        this.id = placeId;
        this.formattedAddress = formattedAddress;
        this.googleMapsUri = googleMapsUri;
        this.reviews = reviews;
        this.fuelOptions = fuelOptions;
        this.currentOpeningHours = currentOpeningHours;
        this.rating = rating;
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
}


