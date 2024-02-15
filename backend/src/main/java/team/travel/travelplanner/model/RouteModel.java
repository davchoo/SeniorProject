package team.travel.travelplanner.model;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import team.travel.travelplanner.util.EncodedPolylineUtils;

import java.time.Instant;

public record RouteModel(String polyline, int[] durations, Instant startTime) {

    public Geometry geometry(GeometryFactory geometryFactory) {
        CoordinateSequence coordinateSequence = EncodedPolylineUtils.decodePolyline(polyline);
        return geometryFactory.createLineString(coordinateSequence);
    }
}
