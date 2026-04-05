package MaidRepository.maid.repository;

import MaidRepository.maid.model.RatingReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RatingReviewRepository extends JpaRepository<RatingReview, Long> {

    Optional<RatingReview> findByBookingId(Long bookingId);

    List<RatingReview> findByMaidIdOrderByCreatedAtDesc(Long maidId);

    List<RatingReview> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndBookingId(Long userId, Long bookingId);

    @Query("SELECT AVG(r.rating) FROM RatingReview r WHERE r.maid.id = :maidId")
    Double findAverageRatingByMaidId(@Param("maidId") Long maidId);

    @Query("SELECT COUNT(r) FROM RatingReview r WHERE r.maid.id = :maidId")
    Long countByMaidId(@Param("maidId") Long maidId);

    List<RatingReview> findTop5ByMaidIdOrderByCreatedAtDesc(Long maidId);
}