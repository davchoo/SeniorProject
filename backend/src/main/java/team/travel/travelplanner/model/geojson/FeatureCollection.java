package team.travel.travelplanner.model.geojson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeatureCollection(List<Feature> features) implements GeoJSONObject {
}
