package team.travel.travelplanner.model.weather;

import java.util.List;

public record RouteWeatherAlertsModel(int[] segmentAlerts, List<WeatherAlertModel> alerts) {
}
