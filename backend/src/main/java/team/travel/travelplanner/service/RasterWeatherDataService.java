package team.travel.travelplanner.service;

import org.locationtech.jts.geom.LineString;
import team.travel.travelplanner.model.weather.RasterWeatherModel;

import java.io.IOException;
import java.time.Instant;

public interface RasterWeatherDataService {
    RasterWeatherModel checkWeather(LineString route, int[] durations, Instant startTime) throws IOException;
}
