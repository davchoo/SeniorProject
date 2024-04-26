package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.travel.travelplanner.entity.County;

import java.util.List;

public interface CountyRepository extends JpaRepository<County, Integer> {
    List<County> findAllByFipsIn(List<String> fips);
}
