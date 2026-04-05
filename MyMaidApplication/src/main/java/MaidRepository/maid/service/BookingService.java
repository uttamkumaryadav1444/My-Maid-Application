package MaidRepository.maid.service;

import MaidRepository.maid.dto.BookingResponseDTO;
import MaidRepository.maid.dto.BookingStatusUpdateDTO;
import MaidRepository.maid.dto.PayAndBookRequestDTO;
import MaidRepository.maid.dto.PaymentVerificationRequestDTO;
import MaidRepository.maid.model.Booking.BookingStatus;
import MaidRepository.maid.model.User;
import java.util.List;
import java.util.Map;

public interface BookingService {

    // ✅ NEW: Create booking with payment
    BookingResponseDTO createBooking(PayAndBookRequestDTO request, Long userId);

    // ✅ NEW: Verify payment
    BookingResponseDTO verifyPayment(PaymentVerificationRequestDTO request);

    // User booking methods
    BookingResponseDTO getBookingById(Long id, User user);
    List<BookingResponseDTO> getUserBookings(User user);
    List<BookingResponseDTO> getBookingsByStatus(User user, BookingStatus status);
    BookingResponseDTO cancelBooking(Long bookingId, User user, String reason);

    // Maid booking methods
    List<BookingResponseDTO> getMaidBookings(Long maidId);
    List<BookingResponseDTO> getPendingBookingsForMaid(Long maidId);
    List<BookingResponseDTO> getTodayBookingsForMaid(Long maidId);
    BookingResponseDTO updateBookingStatus(Long bookingId, Long maidId, BookingStatusUpdateDTO request);

    // Utility methods
    boolean checkMaidAvailability(Long maidId, String date, String time);
    Double calculateBookingAmount(Long maidId, Integer durationHours, Integer numberOfMaids);

    // Categorized bookings method
    Map<String, Object> getUserBookingsCategorized(User user);
}