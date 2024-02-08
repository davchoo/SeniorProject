package team.travel.travelplanner.model;

import java.time.Instant;

public record RouteWeatherFeature(int i, String weatherFeatureType, int forecastDay, Instant fileDate, Instant startTimestamp, Instant endTimestamp) {
}
