package team.travel.travelplanner.controller;

import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.web.bind.annotation.*;
import team.travel.travelplanner.model.CountyModel;
import team.travel.travelplanner.model.RouteModel;
import team.travel.travelplanner.model.weather.RasterWeatherModel;
import team.travel.travelplanner.model.weather.RouteWeatherAlertsModel;
import team.travel.travelplanner.model.weather.SegmentWeatherModel;
import team.travel.travelplanner.model.weather.WeatherFeatureModel;
import team.travel.travelplanner.service.CountyService;
import team.travel.travelplanner.service.RasterWeatherDataService;
import team.travel.travelplanner.service.WeatherAlertService;
import team.travel.travelplanner.service.WeatherDataService;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final GeometryFactory geometryFactory;
    private final CountyService countyService;
    private final RasterWeatherDataService rasterWeatherDataService;
    private final WeatherAlertService weatherAlertService;
    private final WeatherDataService weatherDataService;

    public WeatherController(GeometryFactory geometryFactory, CountyService countyService,
                             RasterWeatherDataService rasterWeatherDataService, WeatherAlertService weatherAlertService,
                             WeatherDataService weatherDataService) {
        this.geometryFactory = geometryFactory;

        this.countyService = countyService;
        this.rasterWeatherDataService = rasterWeatherDataService;
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
        return weatherAlertService.checkRouteWeatherAlerts(route);
    }

    @GetMapping("/county")
    public Map<String, CountyModel> getCounties(@RequestParam("fips_codes") List<String> fipsCodes) {
        return countyService.getCounties(fipsCodes);
    }

    @PostMapping("/raster/check_route")
    public RasterWeatherModel checkRouteRaster(@RequestBody RouteModel route) throws IOException {
        return rasterWeatherDataService.checkWeather(route, "conus", "wx");
    }

    @PostMapping("/raster/check_route/{area}/{dataset}")
    public RasterWeatherModel checkRouteRaster(@RequestBody RouteModel route,
                                               @PathVariable(name = "area") String area,
                                               @PathVariable(name = "dataset") String dataset) throws IOException {
        return rasterWeatherDataService.checkWeather(route, area, dataset);
    }

    @GetMapping("/raster/areas")
    public Collection<String> getRasterAreas() {
        return rasterWeatherDataService.getAvailableAreas();
    }

    @GetMapping("/raster/datasets")
    public Collection<String> getRasterDatasets() {
        return rasterWeatherDataService.getAvailableDatasets();
    }
}
