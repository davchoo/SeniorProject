package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.travel.travelplanner.entity.GasTrip;
import team.travel.travelplanner.entity.User;

import java.util.List;

@Repository
public interface GasTripRepository extends JpaRepository<GasTrip, Long> {
    List<GasTrip> findAllByUser(User user);
}
