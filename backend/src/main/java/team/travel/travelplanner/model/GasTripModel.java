package team.travel.travelplanner.model;

import com.google.maps.model.DirectionsResult;
import team.travel.travelplanner.entity.GasStation;
import team.travel.travelplanner.entity.GasTrip;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record GasTripModel(
        String origin,
        String destination,

        DirectionsResult directionsResult,

        List<GasStation> gasStationList,

        String fuelType,

        BigDecimal totalTripGasPrice,

        double travelersMeterCapacity,

        double tankSizeInGallons,

        double milesPerGallon


) {
    public static GasTripModel fromEntity(GasTrip gasTrip){
        return new GasTripModel(
                gasTrip.getOrigin(),
                gasTrip.getDestination(),
                gasTrip.getDirectionsResult(),
                new ArrayList<>(gasTrip.getGasStationList()),
                gasTrip.getFuelType(),
                gasTrip.getTotalTripGasPrice(),
                gasTrip.getTravelersMeterCapacity(),
                gasTrip.getTankSizeInGallons(),
                gasTrip.getMilesPerGallon()
        );
    }
}
