package team.travel.travelplanner.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import team.travel.travelplanner.model.type.FuelType;

public record CarModel(
        @NotEmpty
        Integer year,
        @NotEmpty
        String make,
        @NotEmpty
        String model,
        @NotNull
        FuelType fuelType,
        @NotEmpty
        Double milesPerGallon,
        @NotEmpty
        Double tankSizeInGallons
){
}
