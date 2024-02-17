package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.travel.travelplanner.entity.Reviews;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
}
