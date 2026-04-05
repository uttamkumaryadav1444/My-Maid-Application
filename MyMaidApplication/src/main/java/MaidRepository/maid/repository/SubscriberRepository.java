package MaidRepository.maid.repository;

import MaidRepository.maid.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    List<Subscriber> findByUserId(String userId);

    Optional<Subscriber> findByUserIdAndEndDateGreaterThanEqual(String userId, LocalDate date);

    boolean existsByUserIdAndEndDateGreaterThanEqual(String userId, LocalDate date);

    @Query("SELECT s FROM Subscriber s WHERE s.userId = :userId AND s.endDate >= CURRENT_DATE AND (s.status IS NULL OR s.status != 'CANCELLED') ORDER BY s.endDate DESC")
    List<Subscriber> findActiveSubscriptions(@Param("userId") String userId);

    @Query("SELECT s FROM Subscriber s WHERE s.userId = :userId ORDER BY s.createdAt DESC")
    List<Subscriber> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
}