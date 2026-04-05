package MaidRepository.maid.chat.model;

import MaidRepository.maid.model.Booking;
import MaidRepository.maid.model.User;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String messageType = "TEXT";

    private Boolean isRead = false;

    private LocalDateTime readAt;

    private String imageUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Long receiverId;

    private String receiverType;

    private Boolean deletedForSender = false;

    private Boolean deletedForReceiver = false;
}