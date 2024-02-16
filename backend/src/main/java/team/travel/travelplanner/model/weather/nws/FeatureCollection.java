package team.travel.travelplanner.model.weather.nws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeatureCollection(List<Feature> features) implements GeoJSONObject {
}
