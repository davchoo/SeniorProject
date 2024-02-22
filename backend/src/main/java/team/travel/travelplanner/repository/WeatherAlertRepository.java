package team.travel.travelplanner.repository;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team.travel.travelplanner.entity.WeatherAlert;
import team.travel.travelplanner.model.weather.SegmentWeatherAlertModel;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface WeatherAlertRepository extends JpaRepository<WeatherAlert, String> {
    @Modifying(flushAutomatically = true)
    @Query("update WeatherAlert wa set wa.outdated = true where wa.id in :ids")
    void markOutdated(List<String> ids);

    void deleteAllByExpiresBefore(Instant time);

    @Query("select wa.id from WeatherAlert wa")
    Set<String> getAllIds();

    @Query(nativeQuery = true)
    List<SegmentWeatherAlertModel> checkRouteWeatherAlerts(Geometry route, int[] durations, Instant startTime);
}
