package MaidRepository.maid.repository;

import MaidRepository.maid.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ==================== EXISTING METHODS ====================

    Optional<User> findByMobile(String mobile);

    Optional<User> findByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByEmail(String email);

    // ==================== LOCATION METHODS ====================

    List<User> findByCity(String city);

    @Query("SELECT u FROM User u WHERE u.currentLatitude IS NOT NULL AND u.currentLongitude IS NOT NULL")
    List<User> findUsersWithLocation();

    @Query("SELECT u FROM User u WHERE u.hasCompletedProfile = true")
    List<User> findUsersWithCompletedProfile();

    @Query("SELECT u FROM User u WHERE u.city = :city AND u.hasCompletedProfile = true")
    List<User> findVerifiedUsersInCity(@Param("city") String city);

    @Query("SELECT COUNT(u) FROM User u WHERE u.hasCompletedProfile = false")
    long countIncompleteProfiles();
}