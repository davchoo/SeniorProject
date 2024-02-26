package team.travel.travelplanner.model;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import jakarta.validation.constraints.NotBlank;

public record GasRequestModel(

        @NotBlank
        Double originLat,

        @NotBlank
        Double originLng,

        @NotBlank
        Double destinationLat,

        @NotBlank
        Double destinationLng,

        @NotBlank
        String type,

        @NotBlank
        Double travelersMeterCapacity
){

}
