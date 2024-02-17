package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.travel.travelplanner.entity.GasStation;

@Repository
public interface GasStationRepository extends JpaRepository<GasStation, Long> {
}
