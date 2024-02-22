package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.travel.travelplanner.entity.County;

public interface CountyRepository extends JpaRepository<County, Integer> {
}
