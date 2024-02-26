package team.travel.travelplanner.model;

import team.travel.travelplanner.entity.FuelOptions;
import team.travel.travelplanner.entity.GasStation;
import team.travel.travelplanner.entity.Reviews;

import java.util.ArrayList;
import java.util.List;

public record GasStationModel(
        String id,
        String name,
        String formattedAddress,
        String googleMapsUri,
        double rating,

        GasStation.Location location,
        List<Reviews> reviews,
        FuelOptions fuelOptions,
        GasStation.CurrentOpeningHours currentOpeningHours
) {
    public static GasStationModel fromEntity(GasStation gasStation) {
        return new GasStationModel(
                gasStation.getPlaceId(),
                gasStation.getName(),
                gasStation.getFormattedAddress(),
                gasStation.getGoogleMapsUri(),
                gasStation.getRating(),
                gasStation.getLocation(),
                new ArrayList<>(gasStation.getReviews()),// Retrieve review IDs
                gasStation.getFuelOptions(),
                gasStation.getCurrentOpeningHours()
        );
    }
}
