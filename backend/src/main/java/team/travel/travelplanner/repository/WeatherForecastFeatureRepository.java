package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.travel.travelplanner.entity.WeatherForecastFeature;

public interface WeatherForecastFeatureRepository extends JpaRepository<WeatherForecastFeature, Long> {
}
