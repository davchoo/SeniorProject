package team.travel.travelplanner.model;

import jakarta.validation.constraints.NotBlank;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import team.travel.travelplanner.util.EncodedPolylineUtils;

public record GasRequestModel(
        @NotBlank
        String polyline,
        @NotBlank
        String startAddress,
        @NotBlank
        String endAddress,
        @NotBlank
        String type,
        double tankSizeInGallons,
        double milesPerGallon
){
        public LineString geometry(GeometryFactory geometryFactory) {
                CoordinateSequence coordinateSequence = EncodedPolylineUtils.decodePolyline(polyline);
                return geometryFactory.createLineString(coordinateSequence);
        }
}
