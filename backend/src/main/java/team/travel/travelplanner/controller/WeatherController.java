package team.travel.travelplanner.controller;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.travel.travelplanner.model.RouteModel;
import team.travel.travelplanner.model.weather.SegmentWeatherModel;
import team.travel.travelplanner.service.WeatherDataService;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherDataService weatherDataService;

    private final GeometryFactory geometryFactory;

    private static final int WGS84_SRID = 4326;

    public WeatherController(WeatherDataService weatherDataService) {
        this.weatherDataService = weatherDataService;
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), WGS84_SRID);
    }

    @PostMapping("/check_route")
    public List<SegmentWeatherModel> checkRoute(@RequestBody RouteModel route) {
        return weatherDataService.checkRouteWeather(route.geometry(geometryFactory), route.durations(), route.startTime());
    }
}
