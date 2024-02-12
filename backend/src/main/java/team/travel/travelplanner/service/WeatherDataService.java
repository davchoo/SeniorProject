package team.travel.travelplanner.service;

import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.model.RouteWeatherFeature;

import java.time.Instant;
import java.util.List;

public interface WeatherDataService {
    List<RouteWeatherFeature> checkRouteWeather(Geometry route, int[] durations, Instant startTime);
}
