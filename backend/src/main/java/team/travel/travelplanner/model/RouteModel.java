package team.travel.travelplanner.model;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import team.travel.travelplanner.util.EncodedPolylineUtils;

import java.time.Instant;

public record RouteModel(String polyline, int[] durations, Instant startTime) {

    public LineString geometry(GeometryFactory geometryFactory) {
        CoordinateSequence coordinateSequence = EncodedPolylineUtils.decodePolyline(polyline);
        return geometryFactory.createLineString(coordinateSequence);
    }
}
