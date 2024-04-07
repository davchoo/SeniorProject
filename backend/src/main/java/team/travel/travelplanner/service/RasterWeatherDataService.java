package team.travel.travelplanner.service;

import team.travel.travelplanner.model.RouteModel;
import team.travel.travelplanner.model.weather.RasterWeatherModel;

import java.io.IOException;
import java.util.Collection;

public interface RasterWeatherDataService {
    RasterWeatherModel checkWeather(RouteModel route, String area, String dataset) throws IOException;

    Collection<String> getAvailableAreas();

    Collection<String> getAvailableDatasets();
}
