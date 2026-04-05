package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.Booking.BookingStatus;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    /**
     *  GET USER BOOKINGS - CATEGORIZED (Ongoing, Completed, Requests)
     * Ye endpoint user ki saari bookings ko categories mein divide karta hai:
     * - Ongoing: PENDING + ACCEPTED bookings
     * - Completed: COMPLETED bookings
     * - Requests: Sirf PENDING bookings (special view with Cancel + Chat buttons)
     */
    @GetMapping("/my-bookings/categorized")
    public ResponseEntity<APIResponse> getMyBookingsCategorized(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract mobile from auth header
            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("User authentication required"));
            }

            // Validate mobile number
            if (!isValidMobile(mobile)) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid mobile number"));
            }

            // Find user by mobile
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + mobile));

            // Get categorized bookings from service
            Map<String, Object> categorizedBookings = bookingService.getUserBookingsCategorized(user);

            return ResponseEntity.ok(APIResponse.success("Bookings fetched successfully", categorizedBookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch bookings: " + e.getMessage()));
        }
    }

    // GET USER BOOKINGS (Simple list - keep for backward compatibility)
    @GetMapping("/my-bookings")
    public ResponseEntity<APIResponse> getUserBookings(@RequestParam String mobile) {
        try {
            if (mobile == null || mobile.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Mobile number is required"));
            }

            String cleanedMobile = mobile.trim();
            if (!isValidMobile(cleanedMobile)) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid mobile number"));
            }

            User user = userRepository.findByMobile(cleanedMobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<BookingResponseDTO> bookings = bookingService.getUserBookings(user);
            return ResponseEntity.ok(APIResponse.success("User bookings fetched", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch bookings: " + e.getMessage()));
        }
    }

    // ✅ GET BOOKINGS BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<APIResponse> getBookingsByStatus(
            @PathVariable String status,
            @RequestParam String mobile) {
        try {
            if (mobile == null || mobile.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Mobile number is required"));
            }

            User user = userRepository.findByMobile(mobile.trim())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            List<BookingResponseDTO> bookings = bookingService.getBookingsByStatus(user, bookingStatus);
            return ResponseEntity.ok(APIResponse.success("Bookings with status: " + status, bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch bookings: " + e.getMessage()));
        }
    }

    // ✅ CANCEL BOOKING (USER) - Sirf PENDING bookings cancel ho sakti hain
    @PostMapping("/{id}/cancel")
    public ResponseEntity<APIResponse> cancelBooking(
            @PathVariable Long id,
            @RequestParam String mobile,
            @RequestParam String reason) {
        try {
            if (mobile == null || mobile.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Mobile number is required"));
            }

            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Cancellation reason is required"));
            }

            User user = userRepository.findByMobile(mobile.trim())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            BookingResponseDTO booking = bookingService.cancelBooking(id, user, reason.trim());
            return ResponseEntity.ok(APIResponse.success("Booking cancelled successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to cancel booking: " + e.getMessage()));
        }
    }

    //  MAID: GET MAID BOOKINGS
    @GetMapping("/maid/{maidId}")
    public ResponseEntity<APIResponse> getMaidBookings(@PathVariable Long maidId) {
        try {
            List<BookingResponseDTO> bookings = bookingService.getMaidBookings(maidId);
            return ResponseEntity.ok(APIResponse.success("Maid bookings", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maid bookings: " + e.getMessage()));
        }
    }

    //  MAID: GET PENDING BOOKINGS
    @GetMapping("/maid/{maidId}/pending")
    public ResponseEntity<APIResponse> getPendingBookingsForMaid(@PathVariable Long maidId) {
        try {
            List<BookingResponseDTO> bookings = bookingService.getPendingBookingsForMaid(maidId);
            return ResponseEntity.ok(APIResponse.success("Pending bookings for maid", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch pending bookings: " + e.getMessage()));
        }
    }

    //  MAID: GET TODAY'S BOOKINGS
    @GetMapping("/maid/{maidId}/today")
    public ResponseEntity<APIResponse> getTodayBookingsForMaid(@PathVariable Long maidId) {
        try {
            List<BookingResponseDTO> bookings = bookingService.getTodayBookingsForMaid(maidId);
            return ResponseEntity.ok(APIResponse.success("Today's bookings", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch today's bookings: " + e.getMessage()));
        }
    }

    //  MAID: UPDATE BOOKING STATUS
    @PutMapping("/maid/{maidId}/{bookingId}/status")
    public ResponseEntity<APIResponse> updateBookingStatus(
            @PathVariable Long maidId,
            @PathVariable Long bookingId,
            @RequestBody BookingStatusUpdateDTO request) {
        try {
            BookingResponseDTO booking = bookingService.updateBookingStatus(bookingId, maidId, request);
            return ResponseEntity.ok(APIResponse.success("Booking status updated", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update booking status: " + e.getMessage()));
        }
    }

    // ✅ CHECK MAID AVAILABILITY
    @GetMapping("/check-availability")
    public ResponseEntity<APIResponse> checkMaidAvailability(
            @RequestParam Long maidId,
            @RequestParam String date,
            @RequestParam String time) {
        try {
            if (date == null || date.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Date is required (YYYY-MM-DD)"));
            }

            if (time == null || time.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Time is required (HH:MM)"));
            }

            boolean isAvailable = bookingService.checkMaidAvailability(maidId, date.trim(), time.trim());
            return ResponseEntity.ok(APIResponse.success("Availability check", isAvailable));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to check availability: " + e.getMessage()));
        }
    }

    // ✅ CALCULATE BOOKING AMOUNT
    @GetMapping("/calculate-amount")
    public ResponseEntity<APIResponse> calculateBookingAmount(
            @RequestParam Long maidId,
            @RequestParam Integer durationHours,
            @RequestParam(required = false) Integer numberOfMaids) {
        try {
            if (durationHours == null || durationHours < 1) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Duration hours must be at least 1"));
            }

            Double amount = bookingService.calculateBookingAmount(maidId, durationHours, numberOfMaids);
            return ResponseEntity.ok(APIResponse.success("Calculated amount", amount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to calculate amount: " + e.getMessage()));
        }
    }

    // HEALTH CHECK ENDPOINT
    @GetMapping("/health")
    public ResponseEntity<APIResponse> healthCheck() {
        try {
            return ResponseEntity.ok(APIResponse.success("Booking API is operational", null));
        } catch (Exception e) {
            return ResponseEntity.ok(APIResponse.error("Booking API has issues: " + e.getMessage()));
        }
    }

    // ---------- Helper Methods ----------

    /**
     * Extract mobile number from Authorization header
     * Supports formats: "Bearer 9876543210", "Mobile 9876543210", or plain "9876543210"
     */
    private String extractMobileFromAuthHeader(String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return null;
            }

            String header = authHeader.trim();

            // Format 1: "Bearer {mobile}"
            if (header.startsWith("Bearer ")) {
                String mobile = header.substring(7).trim();
                return isValidMobile(mobile) ? mobile : null;
            }
            // Format 2: "Mobile {mobile}"
            else if (header.startsWith("Mobile ")) {
                String mobile = header.substring(7).trim();
                return isValidMobile(mobile) ? mobile : null;
            }
            // Format 3: Plain mobile number
            else {
                return isValidMobile(header) ? header : null;
            }
        } catch (Exception e) {
            System.err.println("Error extracting mobile from auth header: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validate Indian mobile number (10 digits starting with 6-9)
     */
    private boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return false;
        }
        String cleaned = mobile.trim();
        return cleaned.matches("^[6-9]\\d{9}$");
    }
}