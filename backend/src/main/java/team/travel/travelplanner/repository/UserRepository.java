package team.travel.travelplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.travel.travelplanner.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsernameOrEmail(String username, String email);

    Users findByEmail(String email);

    Users findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
