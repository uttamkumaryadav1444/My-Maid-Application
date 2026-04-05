package MaidRepository.maid.chat.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SendMessageRequest {
    private Long bookingId;
    private String content;
    private String messageType = "TEXT";
    private MultipartFile image;
}