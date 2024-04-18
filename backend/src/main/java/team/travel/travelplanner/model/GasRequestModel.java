package team.travel.travelplanner.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import team.travel.travelplanner.model.type.FuelType;
import team.travel.travelplanner.util.EncodedPolylineUtils;

public record GasRequestModel(
        @NotBlank
        String polyline,
        @NotBlank
        String startAddress,
        @NotBlank
        String endAddress,
        @NotNull
        FuelType type,
        @NotBlank
        String make,
        @NotBlank
        String model,
        int year,
        double tankSizeInGallons,
        double milesPerGallon,
        double distance,
        String duration
){
        public LineString geometry(GeometryFactory geometryFactory) {
                CoordinateSequence coordinateSequence = EncodedPolylineUtils.decodePolyline(polyline);
                return geometryFactory.createLineString(coordinateSequence);
        }
}
