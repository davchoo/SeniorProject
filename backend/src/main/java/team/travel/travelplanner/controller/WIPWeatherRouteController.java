package team.travel.travelplanner.controller;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.travel.travelplanner.model.Route;
import team.travel.travelplanner.model.RouteWeatherFeature;
import team.travel.travelplanner.repository.WeatherForecastFeatureRepository;
import team.travel.travelplanner.util.EncodedPolylineUtils;

import java.util.List;

@RestController
public class WIPWeatherRouteController {
    private final WeatherForecastFeatureRepository weatherForecastFeatureRepository;

    private final GeometryFactory geometryFactory;

    public WIPWeatherRouteController(WeatherForecastFeatureRepository weatherForecastFeatureRepository) {
        this.weatherForecastFeatureRepository = weatherForecastFeatureRepository;
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    @PostMapping("/check_route_weather")
    public List<RouteWeatherFeature> checkRoute(@RequestBody Route route) {
        CoordinateSequence coordinateSequence = EncodedPolylineUtils.decodePolyline(route.polyline());
        Geometry routeGeometry = geometryFactory.createLineString(coordinateSequence);
        return weatherForecastFeatureRepository.checkRouteWeather(routeGeometry, route.durations(), route.startTime());
    }
}
