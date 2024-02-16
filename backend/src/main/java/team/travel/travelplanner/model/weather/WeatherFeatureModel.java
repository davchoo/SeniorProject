package team.travel.travelplanner.model.weather;

import team.travel.travelplanner.entity.WeatherFeature;
import team.travel.travelplanner.entity.type.WeatherFeatureType;

import java.time.Instant;

public record WeatherFeatureModel(
        int day,
        Instant fileDate,
        Instant validStart,
        Instant validEnd,
        String popUpContent,
        WeatherFeatureType type,
        String geometryWKT
) {
    public static WeatherFeatureModel from(WeatherFeature feature) {
        return new WeatherFeatureModel(
                feature.getDay(),
                feature.getFileDate(),
                feature.getValidStart(),
                feature.getValidEnd(),
                feature.getPopUpContent(),
                feature.getWeatherFeatureType(),
                feature.getGeometry().toString()
        );
    }
}
