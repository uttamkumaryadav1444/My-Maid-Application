package MaidRepository.maid.chat.controller;

import MaidRepository.maid.chat.dto.ChatMessageDTO;
import MaidRepository.maid.chat.dto.ChatConversationDTO;
import MaidRepository.maid.chat.dto.SendMessageRequest;
import MaidRepository.maid.chat.service.ChatService;
import MaidRepository.maid.dto.APIResponse;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<APIResponse> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @ModelAttribute SendMessageRequest request) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User sender = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ChatMessageDTO message = chatService.sendMessage(sender, request);
            return ResponseEntity.ok(APIResponse.success("Message sent", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed: " + e.getMessage()));
        }
    }

    @GetMapping("/booking/{bookingId}/messages")
    public ResponseEntity<APIResponse> getMessages(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long bookingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<ChatMessageDTO> messages = chatService.getBookingMessages(bookingId, user, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("messages", messages.getContent());
            response.put("totalPages", messages.getTotalPages());
            response.put("totalElements", messages.getTotalElements());

            return ResponseEntity.ok(APIResponse.success("Messages fetched", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed: " + e.getMessage()));
        }
    }

    @GetMapping("/conversations")
    public ResponseEntity<APIResponse> getConversations(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<ChatConversationDTO> conversations = chatService.getUserConversations(user);
            return ResponseEntity.ok(APIResponse.success("Conversations fetched", conversations));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed: " + e.getMessage()));
        }
    }

    @PostMapping("/booking/{bookingId}/read")
    public ResponseEntity<APIResponse> markAsRead(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long bookingId) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            chatService.markMessagesAsRead(bookingId, user);
            return ResponseEntity.ok(APIResponse.success("Marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed: " + e.getMessage()));
        }
    }

    @GetMapping("/unread/count")
    public ResponseEntity<APIResponse> getUnreadCount(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            long count = chatService.getUnreadCount(user);
            return ResponseEntity.ok(APIResponse.success("Unread count", Map.of("count", count)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed: " + e.getMessage()));
        }
    }

    private String extractMobileFromAuthHeader(String authHeader) {
        if (authHeader == null) return null;
        String header = authHeader.trim();
        if (header.startsWith("Bearer ")) return header.substring(7).trim();
        if (header.startsWith("Mobile ")) return header.substring(7).trim();
        return header;
    }
}