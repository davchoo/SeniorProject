package team.travel.travelplanner.model.weather;

import java.util.List;
import java.util.Map;

public record RouteWeatherAlertsModel(List<SegmentWeatherAlertModel> segmentAlerts, Map<String, WeatherAlertModel> alerts) {
}
