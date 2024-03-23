package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.travel.travelplanner.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);


    boolean existsByUsername(String username);
}
