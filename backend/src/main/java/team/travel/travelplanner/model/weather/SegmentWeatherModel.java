package team.travel.travelplanner.model.weather;

import team.travel.travelplanner.entity.type.WeatherFeatureType;

import java.time.Instant;

public record SegmentWeatherModel(int segmentId, WeatherFeatureType weatherFeatureType, int forecastDay, Instant fileDate) {
    public SegmentWeatherModel(int i, String weatherFeatureType, int forecastDay, Instant fileDate) {
        this(i, WeatherFeatureType.valueOf(weatherFeatureType), forecastDay, fileDate);
    }
}
