package team.travel.travelplanner.model.weather;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.entity.WeatherFeature;
import team.travel.travelplanner.entity.type.WeatherFeatureType;
import team.travel.travelplanner.serializer.WKB64GeometrySerializer;

import java.time.Instant;

public record WeatherFeatureModel(
        int day,
        Instant fileDate,
        Instant validStart,
        Instant validEnd,
        String popUpContent,
        WeatherFeatureType type,
        @JsonSerialize(using = WKB64GeometrySerializer.class)
        Geometry geometry
) {
    public static WeatherFeatureModel from(WeatherFeature feature) {
        return new WeatherFeatureModel(
                feature.getDay(),
                feature.getFileDate(),
                feature.getValidStart(),
                feature.getValidEnd(),
                feature.getPopUpContent(),
                feature.getWeatherFeatureType(),
                feature.getGeometry()
        );
    }
}
