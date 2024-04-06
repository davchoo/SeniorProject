package team.travel.travelplanner.model;

import jakarta.validation.constraints.NotEmpty;

public record CarModel(
        @NotEmpty
        Integer year,
        @NotEmpty
        String make,
        @NotEmpty
        String model,
        @NotEmpty
        String fuelType,
        @NotEmpty
        Double milesPerGallon,
        @NotEmpty
        Double tankSizeInGallons
){
}
