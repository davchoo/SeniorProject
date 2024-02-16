package team.travel.travelplanner.model.weather.nws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.deserializer.GeometryDeserializer;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Feature(
        String id,
        @JsonDeserialize(using = GeometryDeserializer.class)
        Geometry geometry,
        Map<String, Object> properties) implements GeoJSONObject {
}