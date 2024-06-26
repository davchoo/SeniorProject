package team.travel.travelplanner.repository;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team.travel.travelplanner.entity.WeatherFeature;
import team.travel.travelplanner.model.weather.SegmentWeatherModel;

import java.time.Instant;
import java.util.List;

@Repository
public interface WeatherFeatureRepository extends JpaRepository<WeatherFeature, Long> {
    @Query("select max(f.fileDate) from WeatherFeature f")
    Instant findLatestFileDate();

    List<WeatherFeature> findAllByFileDateAndDay(Instant fileDate, int day);

    @Query("select distinct f.fileDate from WeatherFeature f")
    List<Instant> findAllDistinctFileDates(Sort sort);

    @Query(value = "CALL deduplicate_weather_features()", nativeQuery = true) // HACK?: @Modifying only applies to @Query and not @Procedure
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void deduplicate();

    @Query(nativeQuery = true)
    List<SegmentWeatherModel> checkRouteWeather(Geometry route, int[] durations, Instant startTime);

    @Query(value = "refresh materialized view weather_feature_view", nativeQuery = true)
    @Modifying(flushAutomatically = true)
    void refreshView();
}
