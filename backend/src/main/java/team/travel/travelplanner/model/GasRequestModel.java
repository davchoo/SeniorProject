package team.travel.travelplanner.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
        @DecimalMin("1885")
        int year,
        @Positive
        double tankSizeInGallons,
        @Positive
        double milesPerGallon,
        @Positive
        double distance,
        @NotBlank
        String duration
){
        public LineString geometry(GeometryFactory geometryFactory) {
                CoordinateSequence coordinateSequence = EncodedPolylineUtils.decodePolyline(polyline);
                return geometryFactory.createLineString(coordinateSequence);
        }
}
