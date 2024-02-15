package team.travel.travelplanner.service;

import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.model.weather.SegmentWeatherModel;

import java.time.Instant;
import java.util.List;

public interface WeatherDataService {
    List<SegmentWeatherModel> checkRouteWeather(Geometry route, int[] durations, Instant startTime);
}
