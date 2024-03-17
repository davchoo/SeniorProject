package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.travel.travelplanner.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameOrEmail(String username, String email);

    User findByEmail(String email);

    User findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
