package MaidRepository.maid.chat.Impl;

import MaidRepository.maid.chat.dto.ChatMessageDTO;
import MaidRepository.maid.chat.dto.ChatConversationDTO;
import MaidRepository.maid.chat.dto.SendMessageRequest;
import MaidRepository.maid.chat.model.ChatMessage;
import MaidRepository.maid.chat.repository.ChatMessageRepository;
import MaidRepository.maid.chat.service.ChatService;
import MaidRepository.maid.model.*;
import MaidRepository.maid.repository.BookingRepository;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final MaidRepository maidRepository;
    private final FileStorageService fileStorageService;

    @Override
    public ChatMessageDTO sendMessage(User sender, SendMessageRequest request) throws IOException {
        log.info("Sending message from user: {} for booking: {}", sender.getId(), request.getBookingId());

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + request.getBookingId()));

        // Check if user is part of this booking
        boolean isParticipant = booking.getUser().getId().equals(sender.getId()) ||
                (booking.getMaid() != null && booking.getMaid().getId().equals(sender.getId()));

        if (!isParticipant) {
            throw new RuntimeException("You are not part of this conversation");
        }

        // Determine receiver
        Long receiverId;
        String receiverType;

        if (booking.getUser().getId().equals(sender.getId())) {
            // User is sending to maid
            receiverId = booking.getMaid().getId();
            receiverType = "MAID";
        } else {
            // Maid is sending to user
            receiverId = booking.getUser().getId();
            receiverType = "USER";
        }

        // Create message
        ChatMessage message = new ChatMessage();
        message.setBooking(booking);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        message.setReceiverId(receiverId);
        message.setReceiverType(receiverType);
        message.setIsRead(false);

        // Handle image upload - FIXED: Adding null as second parameter for fileName
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = fileStorageService.storeFile(request.getImage(), null);
            message.setImageUrl(imageUrl);
            message.setMessageType("IMAGE");
        }

        ChatMessage savedMessage = chatMessageRepository.save(message);
        log.info("Message saved with ID: {}", savedMessage.getId());

        return convertToDTO(savedMessage);
    }

    @Override
    public Page<ChatMessageDTO> getBookingMessages(Long bookingId, User user, Pageable pageable) {
        // Verify user has access to this booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        boolean hasAccess = booking.getUser().getId().equals(user.getId()) ||
                (booking.getMaid() != null && booking.getMaid().getId().equals(user.getId()));

        if (!hasAccess) {
            throw new RuntimeException("You don't have access to these messages");
        }

        return chatMessageRepository.findByBookingIdOrderByCreatedAtDesc(bookingId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<ChatConversationDTO> getUserConversations(User user) {
        log.info("Getting conversations for user: {}", user.getId());

        // Get user's bookings
        List<Booking> userBookings;

        // Check if user is a maid or regular user
        Optional<Maid> maidOpt = maidRepository.findByMobile(user.getMobile());
        if (maidOpt.isPresent()) {
            // User is a maid
            Maid maid = maidOpt.get();
            userBookings = bookingRepository.findByMaid(maid);
        } else {
            // User is a regular user
            userBookings = bookingRepository.findByUser(user);
        }

        // Get last messages for conversations
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessages(
                user.getId(), user.getId());

        Map<Long, ChatMessage> lastMessageMap = lastMessages.stream()
                .collect(Collectors.toMap(
                        m -> m.getBooking().getId(),
                        m -> m,
                        (existing, replacement) -> existing
                ));

        List<ChatConversationDTO> conversations = new ArrayList<>();

        for (Booking booking : userBookings) {
            ChatConversationDTO conv = new ChatConversationDTO();
            conv.setBookingId(booking.getId());
            conv.setBookingStatus(booking.getStatus() != null ? booking.getStatus().toString() : "UNKNOWN");

            // FIXED: Convert LocalDate to LocalDateTime by combining with start of day
            if (booking.getServiceDate() != null) {
                conv.setServiceDate(booking.getServiceDate());
                // If you need LocalDateTime, use: LocalDateTime.of(booking.getServiceDate(), LocalTime.MIDNIGHT)
            }

            if (booking.getMaid() != null && booking.getMaid().getServiceType() != null) {
                conv.setServiceType(booking.getMaid().getServiceType().toString());
            }

            // Set other participant info
            if (maidOpt.isPresent()) {
                // Current user is maid, other is customer
                User customer = booking.getUser();
                conv.setOtherUserId(customer.getId());
                conv.setOtherUserName(customer.getFullName());
                conv.setOtherUserProfilePhoto(customer.getProfilePhotoUrl());
                conv.setOtherUserType("USER");
            } else {
                // Current user is customer, other is maid
                Maid maid = booking.getMaid();
                if (maid != null) {
                    conv.setOtherUserId(maid.getId());
                    conv.setOtherUserName(maid.getName());
                    conv.setOtherUserProfilePhoto(maid.getProfilePhotoUrl());
                    conv.setOtherUserType("MAID");
                }
            }

            // Add last message
            ChatMessage lastMsg = lastMessageMap.get(booking.getId());
            if (lastMsg != null) {
                conv.setLastMessage(lastMsg.getContent());
                conv.setLastMessageTime(lastMsg.getCreatedAt());
                conv.setLastMessageSenderId(lastMsg.getSender().getId());
            }

            // Get unread count
            long unreadCount = chatMessageRepository.countUnreadMessages(booking.getId(), user.getId());
            conv.setUnreadCount(unreadCount);

            conversations.add(conv);
        }

        // Sort by last message time (newest first)
        conversations.sort((a, b) -> {
            if (a.getLastMessageTime() == null && b.getLastMessageTime() == null) return 0;
            if (a.getLastMessageTime() == null) return 1;
            if (b.getLastMessageTime() == null) return -1;
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });

        return conversations;
    }

    @Override
    public void markMessagesAsRead(Long bookingId, User user) {
        chatMessageRepository.markAsRead(bookingId, user.getId());
        log.info("Marked messages as read for booking: {}, user: {}", bookingId, user.getId());
    }

    @Override
    public long getUnreadCount(User user) {
        return chatMessageRepository.countTotalUnread(user.getId());
    }

    @Override
    public String uploadImage(MultipartFile file, Long userId) throws IOException {
        // FIXED: Adding null as second parameter for fileName
        return fileStorageService.storeFile(file, null);
    }

    @Override
    public void deleteMessage(Long messageId, User user) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own messages");
        }

        // Soft delete
        message.setDeletedForSender(true);
        chatMessageRepository.save(message);
        log.info("Message deleted by user: {}", user.getId());
    }

    private ChatMessageDTO convertToDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setBookingId(message.getBooking().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setSenderProfilePhoto(message.getSender().getProfilePhotoUrl());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setIsRead(message.getIsRead());
        dto.setReadAt(message.getReadAt());
        dto.setImageUrl(message.getImageUrl());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setReceiverId(message.getReceiverId());
        dto.setReceiverType(message.getReceiverType());
        return dto;
    }
}