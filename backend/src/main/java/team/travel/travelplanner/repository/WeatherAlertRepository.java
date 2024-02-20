package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.travel.travelplanner.entity.WeatherAlert;

public interface WeatherAlertRepository extends JpaRepository<WeatherAlert, String> {
}
