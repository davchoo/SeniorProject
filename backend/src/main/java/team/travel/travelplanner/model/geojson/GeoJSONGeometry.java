package team.travel.travelplanner.model.geojson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.deserializer.GeoJSONGeometryDeserializer;

@JsonDeserialize(using = GeoJSONGeometryDeserializer.class)
public record GeoJSONGeometry(Geometry geometry) implements GeoJSONObject {
}
