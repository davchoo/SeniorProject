package team.travel.travelplanner.service;

import team.travel.travelplanner.model.RouteModel;
import team.travel.travelplanner.model.weather.RouteWeatherAlertsModel;

public interface WeatherAlertService {
    RouteWeatherAlertsModel checkRouteWeatherAlerts(RouteModel route);
}
