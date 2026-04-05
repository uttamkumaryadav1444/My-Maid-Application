package MaidRepository.maid.chat.repository;

import MaidRepository.maid.chat.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByBookingIdOrderByCreatedAtDesc(Long bookingId, Pageable pageable);

    List<ChatMessage> findTop50ByBookingIdOrderByCreatedAtDesc(Long bookingId);

    @Query("SELECT COUNT(c) FROM ChatMessage c WHERE c.booking.id = :bookingId AND c.receiverId = :userId AND c.isRead = false")
    long countUnreadMessages(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM ChatMessage c WHERE c.receiverId = :userId AND c.isRead = false")
    long countTotalUnread(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage c SET c.isRead = true, c.readAt = CURRENT_TIMESTAMP WHERE c.booking.id = :bookingId AND c.receiverId = :userId AND c.isRead = false")
    void markAsRead(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    @Query(value = "SELECT c.* FROM chat_messages c " +
            "INNER JOIN (SELECT MAX(created_at) as max_date, booking_id FROM chat_messages GROUP BY booking_id) grouped " +
            "ON c.booking_id = grouped.booking_id AND c.created_at = grouped.max_date " +
            "WHERE c.booking_id IN (SELECT id FROM bookings WHERE user_id = :userId OR maid_id = :maidId) " +
            "ORDER BY c.created_at DESC", nativeQuery = true)
    List<ChatMessage> findLastMessages(@Param("userId") Long userId, @Param("maidId") Long maidId);
}