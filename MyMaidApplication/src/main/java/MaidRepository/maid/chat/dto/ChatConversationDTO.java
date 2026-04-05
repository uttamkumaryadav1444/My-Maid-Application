package MaidRepository.maid.chat.dto;

import lombok.Data;
import java.time.LocalDate;  // Changed from LocalDateTime
import java.time.LocalDateTime;

@Data
public class ChatConversationDTO {
    private Long bookingId;
    private String bookingStatus;
    private LocalDate serviceDate;  // Changed to LocalDate
    private String serviceType;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserProfilePhoto;
    private String otherUserType;
    private String lastMessage;
    private LocalDateTime lastMessageTime;  // This remains LocalDateTime
    private Long lastMessageSenderId;
    private Long unreadCount;
}