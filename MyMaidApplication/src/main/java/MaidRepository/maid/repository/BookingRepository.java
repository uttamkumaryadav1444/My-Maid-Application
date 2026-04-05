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

    // Find by User
    List<Booking> findByUser(User user);
    Optional<Booking> findByIdAndUser(Long id, User user);
    List<Booking> findByUserAndStatus(User user, BookingStatus status);

    // Find by Maid
    List<Booking> findByMaid(Maid maid);
    Optional<Booking> findByIdAndMaid(Long id, Maid maid);
    List<Booking> findByMaidAndStatusOrderByCreatedAtDesc(Maid maid, BookingStatus status);

    // ✅ ADD THIS METHOD
    Optional<Booking> findByOrderId(String orderId);

    // Find by date
    List<Booking> findByMaidAndServiceDate(Maid maid, LocalDate serviceDate);

    @Query("SELECT b FROM Booking b WHERE b.maid = :maid AND b.serviceDate = CURRENT_DATE")
    List<Booking> findTodayBookingsForMaid(@Param("maid") Maid maid);

    // Check for conflicting bookings
    @Query("SELECT b FROM Booking b WHERE b.maid.id = :maidId AND b.serviceDate = :date " +
            "AND b.status IN ('PENDING', 'ACCEPTED') " +
            "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookings(@Param("maidId") Long maidId,
                                          @Param("date") LocalDate date,
                                          @Param("startTime") LocalTime startTime,
                                          @Param("endTime") LocalTime endTime);
}