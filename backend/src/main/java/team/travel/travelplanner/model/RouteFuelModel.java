package team.travel.travelplanner.model;

import jakarta.validation.constraints.NotBlank;

public record RouteFuelModel(
    @NotBlank String departurePlaceId,

    @NotBlank String arrivalPlaceId,

    @NotBlank double tankSize,

    @NotBlank double mileage

) {
}
