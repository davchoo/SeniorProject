package team.travel.travelplanner.model.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.maps.model.LatLng;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class GoogleGasStation {
    private String id;
    private String displayName;
    private String formattedAddress;
    private LatLng location;
    private List<FuelPrice> fuelPrices;
    private double rating;
    private List<Reviews> reviews;

    private CurrentOpeningHours currentOpeningHours;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public List<FuelPrice> getFuelPrices() {
        return fuelPrices;
    }

    public void setFuelPrices(List<FuelPrice> fuelPrices) {
        this.fuelPrices = fuelPrices;
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

    public void setRating(double rating){
        this.rating = rating;
    }

    public List<Reviews> getReviews(){
        return reviews;
    }

    public void setReviews(List<Reviews> reviews){
        this.reviews = reviews;
    }

    @JsonProperty("displayName")
    private void unpackDisplayName(Map<String, String> displayName) {
        this.displayName = displayName.get("text");
    }

    @JsonProperty("fuelOptions")
    private void unpackFuelOptions(Map<String, List<FuelPrice>> fuelOptions) {
        if (fuelOptions != null) {
            fuelPrices = fuelOptions.get("fuelPrices");
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FuelPrice(
            String type,
            Price price,
            Instant updateTime
    ) {
        public double priceDouble() {
            return Double.parseDouble(price.units) + (price.nanos / 1_000_000_000.0);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Price(
            String currencyCode,
            String units,
            long nanos
    ) {
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
                    "weekdayDescriptions=" + description +
                    '}';
        }

        public void setWeekdayDescriptions(List<String> weekdayDescriptions){
            this.weekdayDescriptions = weekdayDescriptions;
        }
    }

    public static class Reviews {
        private int rating;
        private String name;

        private Text text;

        public Reviews(int rating, String name, Text text) {
            this.rating = rating;
            this.name = name;
            this.text = text;
        }

        public Reviews() {

        }

        @Override
        public String toString() {
            return "Reviews{" +
                    "text=" + //text.toString() +
                    ", rating=" + rating +
                    ", name='" + name + '\'' +
                    '}';
        }

        public String getName(){
            return name;
        }

        public int getRating(){
            return rating;
        }

        public void setRating(int rating){
            this.rating = rating;
        }

        public void setName(String name){
            this.name = name;
        }

        public Text getText() {
            return text;
        }

        public void setText(Text text) {
            this.text = text;
        }

        public static class Text{

            private String text;

            @Override
            public String toString(){
                return text;
            }

            public String getText(){
                return text;
            }

            public void setText(String text){
                this.text = text;
            }
        }
    }
}
