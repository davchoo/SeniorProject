package team.travel.travelplanner.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import team.travel.travelplanner.util.EncodedPolylineUtils;

import java.time.Instant;

public record RouteModel(
        @NotBlank String polyline,
        @NotEmpty int[] durations,
        @NotNull Instant startTime) {

    public LineString geometry(GeometryFactory geometryFactory) {
        CoordinateSequence coordinateSequence = EncodedPolylineUtils.decodePolyline(polyline);
        return geometryFactory.createLineString(coordinateSequence);
    }
}
