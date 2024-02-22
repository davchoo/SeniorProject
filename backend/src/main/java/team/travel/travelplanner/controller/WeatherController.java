package team.travel.travelplanner.controller;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.web.bind.annotation.*;
import team.travel.travelplanner.model.RouteModel;
import team.travel.travelplanner.model.weather.RouteWeatherAlertsModel;
import team.travel.travelplanner.model.weather.SegmentWeatherModel;
import team.travel.travelplanner.model.weather.WeatherFeatureModel;
import team.travel.travelplanner.service.WeatherAlertService;
import team.travel.travelplanner.service.WeatherDataService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private static final int WGS84_SRID = 4326;

    private final GeometryFactory geometryFactory;
    private final WeatherAlertService weatherAlertService;
    private final WeatherDataService weatherDataService;

    public WeatherController(WeatherAlertService weatherAlertService, WeatherDataService weatherDataService) {
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), WGS84_SRID);
        this.weatherAlertService = weatherAlertService;
        this.weatherDataService = weatherDataService;
    }

    @PostMapping("/check_route")
    public List<SegmentWeatherModel> checkRoute(@RequestBody RouteModel route) {
        return weatherDataService.checkRouteWeather(route.geometry(geometryFactory), route.durations(), route.startTime());
    }

    @GetMapping("/features")
    public List<WeatherFeatureModel> getFeatures(@RequestParam("file_date") Instant fileDate, @RequestParam("day") int day) {
        return weatherDataService.getFeatures(fileDate, day);
    }

    @GetMapping("/features/file_dates")
    public List<Instant> getAvailableFileDates() {
        return weatherDataService.getAvailableFileDates();
    }

    @PostMapping("/alerts/check_route")
    public RouteWeatherAlertsModel checkRouteAlerts(@RequestBody RouteModel route) {
        return weatherAlertService.checkRouteWeatherAlerts(route.geometry(geometryFactory), route.durations(), route.startTime());
    }
}
