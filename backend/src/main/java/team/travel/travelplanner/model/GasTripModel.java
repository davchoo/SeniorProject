package team.travel.travelplanner.model;

import team.travel.travelplanner.entity.GasTrip;
import team.travel.travelplanner.util.EncodedPolylineUtils;

import java.util.ArrayList;
import java.util.List;

public record GasTripModel(
        String origin,
        String destination,

        String polyline,

        List<GasStationModel> gasStations,

        String fuelType,

        double totalTripGasPrice,

        double travelersMeterCapacity,

        double tankSizeInGallons,

        double milesPerGallon


) {
    public static GasTripModel fromEntity(GasTrip gasTrip){
        return new GasTripModel(
                gasTrip.getOrigin(),
                gasTrip.getDestination(),
                EncodedPolylineUtils.encodePolyline(gasTrip.getLineString().getCoordinateSequence()),
                new ArrayList<>(gasTrip.getGasStations()),
                gasTrip.getFuelType(),
                gasTrip.getTotalTripGasPrice(),
                gasTrip.getTravelersMeterCapacity(),
                gasTrip.getTankSizeInGallons(),
                gasTrip.getMilesPerGallon()
        );
    }
}
