package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.*;
import MaidRepository.maid.model.Booking.BookingStatus;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.repository.BookingRepository;
import MaidRepository.maid.service.BookingService;
import MaidRepository.maid.service.PriceCalculationService;
import MaidRepository.maid.service.RazorPayService;
import MaidRepository.maid.service.LocationService;  // ✅ ADD THIS IMPORT
import org.slf4j.Logger;  // ✅ ADD THIS IMPORT
import org.slf4j.LoggerFactory;  // ✅ ADD THIS IMPORT
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;  // ✅ ADD THIS IMPORT

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);  // ✅ ADD THIS

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final MaidRepository maidRepository;
    private final BookingRepository bookingRepository;
    private final PriceCalculationService priceCalculationService;
    private final RazorPayService razorPayService;
    private final LocationService locationService;  // ✅ ADD THIS

    // ✅ UPDATE CONSTRUCTOR
    public BookingController(BookingService bookingService,
                             UserRepository userRepository,
                             MaidRepository maidRepository,
                             BookingRepository bookingRepository,
                             PriceCalculationService priceCalculationService,
                             RazorPayService razorPayService,
                             LocationService locationService) {  // ✅ ADD THIS PARAMETER
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.maidRepository = maidRepository;
        this.bookingRepository = bookingRepository;
        this.priceCalculationService = priceCalculationService;
        this.razorPayService = razorPayService;
        this.locationService = locationService;  // ✅ ADD THIS
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
            log.error("Error in getMyBookingsCategorized: {}", e.getMessage());
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
            log.error("Error in getUserBookings: {}", e.getMessage());
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
            log.error("Error in getBookingsByStatus: {}", e.getMessage());
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
            log.error("Error in cancelBooking: {}", e.getMessage());
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
            log.error("Error in getMaidBookings: {}", e.getMessage());
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
            log.error("Error in getPendingBookingsForMaid: {}", e.getMessage());
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
            log.error("Error in getTodayBookingsForMaid: {}", e.getMessage());
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
            log.error("Error in updateBookingStatus: {}", e.getMessage());
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
            log.error("Error in checkMaidAvailability: {}", e.getMessage());
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
            log.error("Error in calculateBookingAmount: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to calculate amount: " + e.getMessage()));
        }
    }

    // ✅ GET ONGOING BOOKINGS - Fixed version
    @GetMapping("/ongoing")
    public ResponseEntity<APIResponse> getOngoingBookings(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("User authentication required"));
            }

            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Get all bookings
            List<BookingResponseDTO> allBookings = bookingService.getUserBookings(user);

            // Filter for ongoing bookings (PENDING, ACCEPTED)
            List<BookingResponseDTO> ongoingBookings = allBookings.stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.PENDING ||
                            booking.getStatus() == BookingStatus.ACCEPTED)
                    .filter(booking -> booking.getServiceDate().isAfter(LocalDate.now().minusDays(1)))
                    .sorted((a, b) -> b.getServiceDate().compareTo(a.getServiceDate()))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("ongoing", ongoingBookings);
            response.put("count", ongoingBookings.size());

            // Add location distance if available
            if (user.getCurrentLatitude() != null && user.getCurrentLongitude() != null) {
                for (BookingResponseDTO booking : ongoingBookings) {
                    if (booking.getLatitude() != null && booking.getLongitude() != null) {
                        try {
                            ORSLocationDTO distance = locationService.calculateDistance(
                                    user.getCurrentLatitude(), user.getCurrentLongitude(),
                                    booking.getLatitude(), booking.getLongitude()
                            );
                            booking.setDistanceKm(distance.getDistanceKm());
                            booking.setTravelTimeMinutes(distance.getDurationMinutes());
                        } catch (Exception e) {
                            log.warn("Failed to calculate distance for booking {}: {}", booking.getId(), e.getMessage());
                        }
                    }
                }
            }

            return ResponseEntity.ok(APIResponse.success("Ongoing bookings fetched", response));

        } catch (Exception e) {
            log.error("Failed to fetch ongoing bookings: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch ongoing bookings: " + e.getMessage()));
        }
    }

    // HEALTH CHECK ENDPOINT
    @GetMapping("/health")
    public ResponseEntity<APIResponse> healthCheck() {
        try {
            return ResponseEntity.ok(APIResponse.success("Booking API is operational", null));
        } catch (Exception e) {
            log.error("Health check error: {}", e.getMessage());
            return ResponseEntity.ok(APIResponse.error("Booking API has issues: " + e.getMessage()));
        }
    }

    // ==================== PAYMENT & BOOKING METHODS ====================

    /**
     * Create RazorPay order before payment
     * POST /api/bookings/create-order
     */
    @PostMapping("/create-order")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PayAndBookRequestDTO request) {

        try {
            // Get user
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Get maid
            Maid maid = maidRepository.findById(request.getMaidId())
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            // Calculate amount
            double amount = priceCalculationService.calculateAmount(
                    maid,
                    request.getBookingType(),
                    request.getHours(),
                    request.getDays(),
                    request.getWeeks(),
                    request.getMonths()
            );

            // Create RazorPay order
            String orderId = razorPayService.createOrder(
                    amount,
                    "INR",
                    "booking_" + System.currentTimeMillis()
            );

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("amount", amount);
            response.put("currency", "INR");
            response.put("key", razorPayService.getRazorpayKeyId());
            response.put("description", priceCalculationService.getBookingDescription(
                    request.getBookingType(),
                    request.getHours(),
                    request.getDays(),
                    request.getWeeks(),
                    request.getMonths()
            ));
            response.put("customerName", user.getFullName());
            response.put("customerEmail", user.getEmail());
            response.put("customerMobile", user.getMobile());

            return ResponseEntity.ok(APIResponse.success("Order created", response));

        } catch (Exception e) {
            log.error("Order creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Order creation failed: " + e.getMessage()));
        }
    }

    /**
     * Verify payment and create booking
     * POST /api/bookings/pay-and-book
     */
    @PostMapping("/pay-and-book")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> payAndBook(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PayAndBookRequestDTO request) {

        try {
            // Step 1: Get user
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Step 2: Verify payment signature
            boolean isValid = razorPayService.verifyPayment(
                    request.getOrderId(),
                    request.getPaymentId(),
                    request.getSignature()
            );

            if (!isValid) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Payment verification failed"));
            }

            // Step 3: Get maid
            Maid maid = maidRepository.findById(request.getMaidId())
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            // Step 4: Calculate total hours
            int totalHours = priceCalculationService.calculateTotalHours(
                    request.getBookingType(),
                    request.getHours(),
                    request.getDays(),
                    request.getWeeks(),
                    request.getMonths()
            );

            // Step 5: Calculate end time
            LocalTime endTime = request.getStartTime().plusHours(totalHours);

            // Step 6: Create booking
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setMaid(maid);
            Booking.BookingType bookingType = Booking.BookingType.valueOf(request.getBookingType().toUpperCase());
            booking.setServiceDate(request.getServiceDate());
            booking.setStartTime(request.getStartTime());
            booking.setEndTime(endTime);
            booking.setDurationHours(totalHours);
            booking.setTotalAmount(request.getTotalAmount());
            booking.setPaymentId(request.getPaymentId());
            booking.setOrderId(request.getOrderId());
            booking.setPaymentStatus("PAID");
            booking.setStatus(BookingStatus.PENDING);
            booking.setCustomerName(user.getFullName());
            booking.setCustomerMobile(user.getMobile());
            booking.setCustomerEmail(user.getEmail());

            // Save booking
            Booking savedBooking = bookingRepository.save(booking);

            return ResponseEntity.ok(APIResponse.success("Booking confirmed", convertToDTO(savedBooking)));

        } catch (Exception e) {
            log.error("Booking failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Booking failed: " + e.getMessage()));
        }
    }

    // ---------- Helper Methods ----------

    /**
     * Extract mobile number from Authorization header
     */
    private String extractMobileFromAuthHeader(String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return null;
            }

            String header = authHeader.trim();

            if (header.startsWith("Bearer ")) {
                String token = header.substring(7).trim();
                if (token.startsWith("token-")) {
                    String[] parts = token.split("-");
                    if (parts.length >= 2) {
                        return parts[1];
                    }
                }
                return isValidMobile(token) ? token : null;
            } else if (header.startsWith("Mobile ")) {
                String mobile = header.substring(7).trim();
                return isValidMobile(mobile) ? mobile : null;
            } else {
                return isValidMobile(header) ? header : null;
            }
        } catch (Exception e) {
            log.error("Error extracting mobile from auth header: {}", e.getMessage());
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

    /**
     * Helper method to convert Booking to BookingResponseDTO
     */
    private BookingResponseDTO convertToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getFullName());
        dto.setMaidId(booking.getMaid().getId());
        dto.setMaidName(booking.getMaid().getName());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerMobile(booking.getCustomerMobile());
        dto.setLocation(booking.getLocation());
        dto.setServiceDate(booking.getServiceDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setDurationHours(booking.getDurationHours());
        dto.setStatus(booking.getStatus());
        dto.setTotalAmount(booking.getTotalAmount());

        // NEW FIELDS
        dto.setPaymentId(booking.getPaymentId());
        dto.setPaymentStatus(booking.getPaymentStatus());
        if (booking.getBookingType() != null) {
            dto.setBookingType(booking.getBookingType().toString());
        }

        // Optional fields
        dto.setMaidProfilePhoto(booking.getMaid().getProfilePhotoUrl());
        if (booking.getMaid().getServiceType() != null) {
            dto.setServiceType(ServiceType.valueOf(booking.getMaid().getServiceType().toString()));
        }

        // Helper flags
        dto.setCanCancel(booking.getStatus() == BookingStatus.PENDING);
        dto.setCanChat(true);
        dto.setCanRate(booking.getStatus() == BookingStatus.COMPLETED);

        return dto;
    }
}