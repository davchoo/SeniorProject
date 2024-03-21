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
}
