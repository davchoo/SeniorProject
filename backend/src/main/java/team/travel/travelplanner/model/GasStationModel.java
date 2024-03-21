package team.travel.travelplanner.model;

import com.google.maps.model.LatLng;
import team.travel.travelplanner.model.google.GoogleGasStation;

import java.util.Map;
import java.util.stream.Collectors;

public record GasStationModel(
        String id,
        String name,
        Map<String, Double> prices,
        LatLng location
) {
    public static GasStationModel from(GoogleGasStation gasStation) {
        return new GasStationModel(
                gasStation.getId(),
                gasStation.getDisplayName(),
                gasStation.getFuelPrices().stream()
                        .collect(Collectors.toMap(
                                GoogleGasStation.FuelPrice::type,
                                GoogleGasStation.FuelPrice::priceDouble)
                        ),
                gasStation.getLocation() // TODO make null?
        );
    }
}
