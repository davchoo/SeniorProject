package team.travel.travelplanner.model;

import team.travel.travelplanner.entity.type.WeatherFeatureType;

import java.time.Instant;

public record RouteWeatherFeature(int i, WeatherFeatureType weatherFeatureType, int forecastDay, Instant fileDate, Instant startTimestamp, Instant endTimestamp) {
    public RouteWeatherFeature(int i, String weatherFeatureType, int forecastDay, Instant fileDate, Instant startTimestamp, Instant endTimestamp) {
        this(i, WeatherFeatureType.valueOf(weatherFeatureType), forecastDay, fileDate, startTimestamp, endTimestamp);
    }
}
