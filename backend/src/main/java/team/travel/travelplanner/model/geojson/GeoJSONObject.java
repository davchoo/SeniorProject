package team.travel.travelplanner.model.geojson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FeatureCollection.class, name = "FeatureCollection"),
        @JsonSubTypes.Type(value = Feature.class, name = "Feature"),
        @JsonSubTypes.Type(value = GeoJSONGeometry.class, name = "Point"),
        @JsonSubTypes.Type(value = GeoJSONGeometry.class, name = "MultiPoint"),
        @JsonSubTypes.Type(value = GeoJSONGeometry.class, name = "LineString"),
        @JsonSubTypes.Type(value = GeoJSONGeometry.class, name = "MultiLineString"),
        @JsonSubTypes.Type(value = GeoJSONGeometry.class, name = "Polygon"),
        @JsonSubTypes.Type(value = GeoJSONGeometry.class, name = "MultiPolygon"),
        @JsonSubTypes.Type(value = GeoJSONGeometry.class, name = "GeometryCollection")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public interface GeoJSONObject {
}
