package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import team.travel.travelplanner.entity.WeatherForecastFeature;

import java.time.ZonedDateTime;

@Repository
public interface WeatherForecastFeatureRepository extends JpaRepository<WeatherForecastFeature, Long> {
    @Query("select max(f.fileDate) from WeatherForecastFeature f")
    ZonedDateTime findLatestFileDate();

    @Procedure("deduplicate_weather_forecast_features")
    void deduplicate();
}
