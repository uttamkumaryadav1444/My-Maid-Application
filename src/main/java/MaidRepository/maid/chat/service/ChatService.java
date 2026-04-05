package MaidRepository.maid.chat.service;

import MaidRepository.maid.chat.dto.ChatMessageDTO;
import MaidRepository.maid.chat.dto.ChatConversationDTO;
import MaidRepository.maid.chat.dto.SendMessageRequest;
import MaidRepository.maid.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ChatService {
    ChatMessageDTO sendMessage(User sender, SendMessageRequest request) throws IOException;
    Page<ChatMessageDTO> getBookingMessages(Long bookingId, User user, Pageable pageable);
    List<ChatConversationDTO> getUserConversations(User user);
    void markMessagesAsRead(Long bookingId, User user);
    long getUnreadCount(User user);
    String uploadImage(MultipartFile file, Long userId) throws IOException;
    void deleteMessage(Long messageId, User user);
}