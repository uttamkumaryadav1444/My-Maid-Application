package MaidRepository.maid.chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private Long id;
    private Long bookingId;
    private Long senderId;
    private String senderName;
    private String senderProfilePhoto;
    private String content;
    private String messageType;
    private Boolean isRead;
    private LocalDateTime readAt;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Long receiverId;
    private String receiverType;
}