package MaidRepository.maid.repository;

import MaidRepository.maid.model.Booking;
import MaidRepository.maid.model.Booking.BookingStatus;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ==================== USER BOOKINGS ====================

    List<Booking> findByUser(User user);

    List<Booking> findByUserAndStatus(User user, BookingStatus status);

    Optional<Booking> findByIdAndUser(Long id, User user);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.serviceDate DESC, b.startTime DESC")
    List<Booking> findByUserIdOrderByDateDesc(@Param("userId") Long userId);


    // ==================== MAID BOOKINGS ====================

    List<Booking> findByMaid(Maid maid);

    List<Booking> findByMaidAndStatus(Maid maid, BookingStatus status);

    Optional<Booking> findByIdAndMaid(Long id, Maid maid);

    List<Booking> findByMaidAndStatusOrderByCreatedAtDesc(Maid maid, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.maid.id = :maidId ORDER BY b.serviceDate DESC, b.startTime DESC")
    List<Booking> findByMaidIdOrderByDateDesc(@Param("maidId") Long maidId);


    // ==================== DATE-BASED QUERIES ====================

    List<Booking> findByMaidAndServiceDate(Maid maid, LocalDate serviceDate);

    List<Booking> findByMaidAndServiceDateAndStatusIn(
            Maid maid,
            LocalDate serviceDate,
            List<BookingStatus> status);

    @Query("SELECT b FROM Booking b WHERE b.maid = :maid AND b.serviceDate = CURRENT_DATE " +
            "AND b.status IN ('PENDING', 'ACCEPTED') ORDER BY b.startTime")
    List<Booking> findTodayBookingsForMaid(@Param("maid") Maid maid);

    @Query("SELECT b FROM Booking b WHERE b.maid.id = :maidId " +
            "AND b.serviceDate = :date ORDER BY b.startTime")
    List<Booking> findBookingsByMaidAndDate(@Param("maidId") Long maidId, @Param("date") LocalDate date);

    List<Booking> findByServiceDateBetween(LocalDate startDate, LocalDate endDate);


    // ==================== CONFLICT DETECTION (CRITICAL) ====================

    /**
     * Find bookings for a specific maid on a given date that overlap with the time range.
     * Only includes bookings with status that block the slot (PENDING, ACCEPTED).
     */
    @Query("SELECT b FROM Booking b WHERE b.maid.id = :maidId " +
            "AND b.serviceDate = :date " +
            "AND b.status IN ('PENDING', 'ACCEPTED') " +
            "AND (b.startTime < :endTime AND b.endTime > :startTime)")
    List<Booking> findConflictingBookings(@Param("maidId") Long maidId,
                                          @Param("date") LocalDate date,
                                          @Param("startTime") LocalTime startTime,
                                          @Param("endTime") LocalTime endTime);

    /**
     * Check if a specific time slot is available for a maid
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.maid.id = :maidId " +
            "AND b.serviceDate = :date " +
            "AND b.status IN ('PENDING', 'ACCEPTED') " +
            "AND (b.startTime < :endTime AND b.endTime > :startTime)")
    boolean isTimeSlotBooked(@Param("maidId") Long maidId,
                             @Param("date") LocalDate date,
                             @Param("startTime") LocalTime startTime,
                             @Param("endTime") LocalTime endTime);


    // ==================== COUNT QUERIES ====================

    long countByMaidAndStatus(Maid maid, BookingStatus status);

    long countByUserAndStatus(User user, BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.maid.id = :maidId AND b.status = :status")
    long countByMaidIdAndStatus(@Param("maidId") Long maidId, @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status);


    // ==================== STATUS UPDATE HELPER ====================

    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.serviceDate < CURRENT_DATE")
    List<Booking> findOldBookingsByStatus(@Param("status") BookingStatus status);


    // ==================== CATEGORIZED BOOKINGS HELPER ====================

    /**
     * Get all bookings for a user with specific statuses (for categorization)
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status IN :statuses " +
            "ORDER BY b.serviceDate DESC, b.startTime DESC")
    List<Booking> findByUserIdAndStatusIn(@Param("userId") Long userId,
                                          @Param("statuses") List<BookingStatus> statuses);

    /**
     * Get counts of bookings by status for a user
     */
    @Query("SELECT b.status, COUNT(b) FROM Booking b WHERE b.user.id = :userId " +
            "GROUP BY b.status")
    List<Object[]> countByStatusForUser(@Param("userId") Long userId);
}