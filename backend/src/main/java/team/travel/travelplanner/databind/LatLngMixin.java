package team.travel.travelplanner.databind;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class LatLngMixin {
    LatLngMixin(@JsonProperty("latitude") double lat, @JsonProperty("longitude") double lng) {}
}
