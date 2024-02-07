package team.travel.travelplanner.repository;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team.travel.travelplanner.entity.WeatherForecastFeature;
import team.travel.travelplanner.model.RouteWeatherFeature;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface WeatherForecastFeatureRepository extends JpaRepository<WeatherForecastFeature, Long> {
    @Query("select max(f.fileDate) from WeatherForecastFeature f")
    ZonedDateTime findLatestFileDate();

    @Query(value = "CALL deduplicate_weather_forecast_features()", nativeQuery = true) // HACK?: @Modifying only applies to @Query and not @Procedure
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void deduplicate();

    @Query(nativeQuery = true)
    List<RouteWeatherFeature> checkRouteWeather(Geometry route, int[] durations, Instant startTime);
}
