package team.travel.travelplanner.service;

import org.locationtech.jts.geom.Geometry;
import team.travel.travelplanner.model.weather.RouteWeatherAlertsModel;

import java.time.Instant;

public interface WeatherAlertService {
    RouteWeatherAlertsModel checkRouteWeatherAlerts(Geometry route, int[] durations, Instant startTime);
}
