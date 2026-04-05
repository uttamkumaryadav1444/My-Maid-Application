package MaidRepository.maid.impl;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.*;
import MaidRepository.maid.model.Booking.BookingStatus;
import MaidRepository.maid.model.Booking.BookingType;
import MaidRepository.maid.repository.BookingRepository;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.BookingService;
import MaidRepository.maid.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final MaidRepository maidRepository;
    private final EmailService emailService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              MaidRepository maidRepository,
                              EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.maidRepository = maidRepository;
        this.emailService = emailService;
    }

    // ==================== CREATE BOOKING WITH PAYMENT ====================

    @Override
    @Transactional
    public BookingResponseDTO createBooking(PayAndBookRequestDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Maid maid = maidRepository.findById(request.getMaidId())
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

        // Check maid availability
        if (!maid.getIsAvailable()) {
            throw new IllegalArgumentException("Maid is not available for booking");
        }

        // Create order ID for payment
        String orderId = "ORD_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setMaid(maid);
        booking.setServiceDate(request.getServiceDate());
        booking.setStartTime(request.getStartTime());

        // Convert String to BookingType enum
        if (request.getBookingType() != null) {
            try {
                BookingType bookingType = BookingType.valueOf(request.getBookingType().toUpperCase());
                booking.setBookingType(bookingType);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid booking type: " + request.getBookingType() +
                        ". Allowed values: HOURLY, DAILY, WEEKLY, MONTHLY");
            }
        }

        booking.setTotalAmount(request.getTotalAmount());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus("PENDING");
        booking.setOrderId(orderId);
        booking.setLocation(request.getAddress());
        booking.setRequirements(request.getSpecialInstructions());

        // Set customer details
        booking.setCustomerName(user.getFullName());
        booking.setCustomerEmail(user.getEmail());
        booking.setCustomerMobile(user.getMobile());

        // Set duration based on booking type
        if ("HOURLY".equalsIgnoreCase(request.getBookingType())) {
            booking.setDurationHours(request.getHours() != null ? request.getHours() : 1);
            booking.setEndTime(request.getStartTime().plusHours(booking.getDurationHours()));
        } else if ("DAILY".equalsIgnoreCase(request.getBookingType())) {
            booking.setDurationHours(request.getDays() != null ? request.getDays() * 8 : 8);
            booking.setEndTime(request.getStartTime().plusHours(booking.getDurationHours()));
        } else if ("WEEKLY".equalsIgnoreCase(request.getBookingType())) {
            int days = request.getWeeks() != null ? request.getWeeks() * 7 : 7;
            booking.setDurationHours(days * 8);
            booking.setEndTime(request.getStartTime().plusHours(booking.getDurationHours()));
        } else if ("MONTHLY".equalsIgnoreCase(request.getBookingType())) {
            int days = request.getMonths() != null ? request.getMonths() * 30 : 30;
            booking.setDurationHours(days * 8);
            booking.setEndTime(request.getStartTime().plusHours(booking.getDurationHours()));
        }

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }

    // ==================== ✅ ADD THIS MISSING METHOD ====================

    @Override
    @Transactional
    public BookingResponseDTO verifyPayment(PaymentVerificationRequestDTO request) {
        // Find booking by order ID or booking ID
        Booking booking;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + request.getBookingId()));
        } else if (request.getOrderId() != null) {
            booking = bookingRepository.findByOrderId(request.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found with orderId: " + request.getOrderId()));
        } else {
            throw new IllegalArgumentException("Booking ID or Order ID required");
        }

        // Verify payment signature (you can implement actual verification logic here)
        // For now, we'll assume payment is successful

        booking.setPaymentStatus("COMPLETED");
        booking.setPaymentId(request.getPaymentId());
        booking.setStatus(BookingStatus.ACCEPTED); // or CONFIRMED if you have that status

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDTO(updatedBooking);
    }

    // ==================== USER BOOKING METHODS ====================

    @Override
    public BookingResponseDTO getBookingById(Long id, User user) {
        Booking booking = bookingRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        return convertToDTO(booking);
    }

    @Override
    public List<BookingResponseDTO> getUserBookings(User user) {
        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getBookingsByStatus(User user, BookingStatus status) {
        List<Booking> bookings = bookingRepository.findByUserAndStatus(user, status);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId, User user, String reason) {
        Booking booking = bookingRepository.findByIdAndUser(bookingId, user)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Cannot cancel booking with status: " + booking.getStatus() +
                            ". Only PENDING bookings can be cancelled."
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);

        Booking cancelledBooking = bookingRepository.save(booking);
        return convertToDTO(cancelledBooking);
    }

    // ==================== MAID BOOKING METHODS ====================

    @Override
    public List<BookingResponseDTO> getMaidBookings(Long maidId) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

        List<Booking> bookings = bookingRepository.findByMaid(maid);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getPendingBookingsForMaid(Long maidId) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

        List<Booking> bookings = bookingRepository.findByMaidAndStatusOrderByCreatedAtDesc(
                maid, BookingStatus.PENDING);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getTodayBookingsForMaid(Long maidId) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

        List<Booking> bookings = bookingRepository.findTodayBookingsForMaid(maid);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBookingStatus(Long bookingId, Long maidId, BookingStatusUpdateDTO request) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

        Booking booking = bookingRepository.findByIdAndMaid(bookingId, maid)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus(request.getStatus());

        if (request.getNotes() != null) {
            booking.setMaidNotes(request.getNotes());
        }

        if (request.getStatus() == BookingStatus.CANCELLED && request.getCancellationReason() != null) {
            booking.setCancellationReason(request.getCancellationReason());
        }

        Booking updatedBooking = bookingRepository.save(booking);

        // Send email notification to user when booking is accepted
        if (request.getStatus() == BookingStatus.ACCEPTED) {
            try {
                emailService.sendBookingConfirmation(
                        booking.getCustomerEmail(),
                        booking.getCustomerName(),
                        maid.getName(),
                        booking.getServiceDate(),
                        booking.getStartTime()
                );
            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }

        return convertToDTO(updatedBooking);
    }

    // ==================== UTILITY METHODS ====================

    @Override
    public boolean checkMaidAvailability(Long maidId, String date, String time) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

        if (!maid.getIsAvailable()) {
            return false;
        }

        LocalDate serviceDate = LocalDate.parse(date);
        LocalTime serviceTime = LocalTime.parse(time);

        List<Booking> existingBookings = bookingRepository.findByMaidAndServiceDate(maid, serviceDate);

        for (Booking booking : existingBookings) {
            if (booking.getStatus() == BookingStatus.ACCEPTED ||
                    booking.getStatus() == BookingStatus.PENDING) {
                LocalTime bookingEnd = booking.getEndTime();
                if (serviceTime.isBefore(bookingEnd) &&
                        serviceTime.plusHours(1).isAfter(booking.getStartTime())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Double calculateBookingAmount(Long maidId, Integer durationHours, Integer numberOfMaids) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

        if (maid.getHourlyRate() == null) {
            throw new IllegalArgumentException("Maid hourly rate not set");
        }

        return maid.getHourlyRate() * durationHours * (numberOfMaids != null ? numberOfMaids : 1);
    }

    // ==================== CATEGORIZED BOOKINGS METHOD ====================

    @Override
    public Map<String, Object> getUserBookingsCategorized(User user) {
        List<Booking> allBookings = bookingRepository.findByUser(user);

        List<BookingResponseDTO> ongoing = new ArrayList<>();
        List<BookingResponseDTO> completed = new ArrayList<>();
        List<BookingResponseDTO> requests = new ArrayList<>();
        List<BookingResponseDTO> cancelled = new ArrayList<>();

        for (Booking booking : allBookings) {
            BookingResponseDTO dto = convertToDTO(booking);

            if (booking.getStatus() == BookingStatus.PENDING) {
                ongoing.add(dto);
                requests.add(dto);
            } else if (booking.getStatus() == BookingStatus.ACCEPTED) {
                ongoing.add(dto);
            } else if (booking.getStatus() == BookingStatus.COMPLETED) {
                completed.add(dto);
            } else if (booking.getStatus() == BookingStatus.CANCELLED ||
                    booking.getStatus() == BookingStatus.REJECTED) {
                cancelled.add(dto);
            }
        }

        // Sort all lists by date (newest first)
        ongoing.sort((a, b) -> b.getServiceDate().compareTo(a.getServiceDate()));
        completed.sort((a, b) -> b.getServiceDate().compareTo(a.getServiceDate()));
        requests.sort((a, b) -> b.getServiceDate().compareTo(a.getServiceDate()));
        cancelled.sort((a, b) -> b.getServiceDate().compareTo(a.getServiceDate()));

        // Prepare counts
        Map<String, Integer> counts = new HashMap<>();
        counts.put("ongoing", ongoing.size());
        counts.put("completed", completed.size());
        counts.put("requests", requests.size());
        counts.put("cancelled", cancelled.size());

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("ongoing", ongoing);
        response.put("completed", completed);
        response.put("requests", requests);
        response.put("cancelled", cancelled);
        response.put("counts", counts);

        return response;
    }

    // ==================== DTO CONVERTER ====================

    private BookingResponseDTO convertToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getFullName());
        dto.setMaidId(booking.getMaid().getId());
        dto.setMaidName(booking.getMaid().getName());
        dto.setMaidProfilePhoto(booking.getMaid().getProfilePhotoUrl());
        dto.setServiceType(booking.getMaid().getServiceType());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerMobile(booking.getCustomerMobile());
        dto.setLocation(booking.getLocation());
        dto.setLatitude(booking.getLatitude());
        dto.setLongitude(booking.getLongitude());
        dto.setRequirements(booking.getRequirements());
        dto.setNumberOfMaids(booking.getNumberOfMaids());
        dto.setServiceDate(booking.getServiceDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setDurationHours(booking.getDurationHours());
        dto.setStatus(booking.getStatus());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setMaidNotes(booking.getMaidNotes());
        dto.setCancellationReason(booking.getCancellationReason());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        dto.setPaymentId(booking.getPaymentId());
        dto.setPaymentStatus(booking.getPaymentStatus());

        if (booking.getBookingType() != null) {
            dto.setBookingType(booking.getBookingType().toString());
        }

        // Helper flags
        dto.setCanCancel(booking.getStatus() == BookingStatus.PENDING);
        dto.setCanChat(true);
        dto.setCanRate(booking.getStatus() == BookingStatus.COMPLETED &&
                booking.getRatingReview() == null);

        return dto;
    }
}