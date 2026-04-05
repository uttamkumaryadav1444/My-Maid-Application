package MaidRepository.maid.controller;

import MaidRepository.maid.dto.APIResponse;
import MaidRepository.maid.dto.RatingReviewDTO.*;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.RatingReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingReviewController {

    private final RatingReviewService ratingReviewService;
    private final UserRepository userRepository;

    public RatingReviewController(RatingReviewService ratingReviewService,
                                  UserRepository userRepository) {
        this.ratingReviewService = ratingReviewService;
        this.userRepository = userRepository;
    }

    // ✅ TEST ENDPOINT - Simple and working
    @PostMapping("/test-create")
    public ResponseEntity<APIResponse> createTestRating(@RequestBody TestCreateRequest request) {
        try {
            System.out.println("=== TEST CREATE RATING ===");
            System.out.println("Booking ID: " + request.bookingId);
            System.out.println("Rating: " + request.rating);
            System.out.println("Comment: " + request.comment);

            // Find any user
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("No users found in database"));
            }

            User user = users.get(0);
            System.out.println("Using user: " + user.getFullName() + " (ID: " + user.getId() + ")");

            // Create rating request
            RatingRequestDTO ratingRequest = new RatingRequestDTO();
            ratingRequest.setBookingId(request.bookingId);
            ratingRequest.setRating(request.rating);
            ratingRequest.setComment(request.comment);

            RatingResponseDTO rating = ratingReviewService.createRating(user, ratingRequest);

            return ResponseEntity.ok(APIResponse.success("Rating created successfully (TEST)", rating));
        } catch (Exception e) {
            System.err.println("ERROR in createTestRating: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(APIResponse.error("Error: " + e.getMessage()));
        }
    }

    // ✅ MAIN CREATE ENDPOINT
    @PostMapping("/create")
    public ResponseEntity<APIResponse> createRating(@RequestBody CreateRatingRequest request) {
        try {
            System.out.println("=== CREATE RATING ===");
            System.out.println("Mobile: " + request.mobile);
            System.out.println("Booking ID: " + request.bookingId);

            if (request.mobile == null || request.mobile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Mobile number is required"));
            }

            User user = userRepository.findByMobile(request.mobile)
                    .orElseThrow(() -> new RuntimeException("User not found with mobile: " + request.mobile));

            RatingRequestDTO ratingRequest = new RatingRequestDTO();
            ratingRequest.setBookingId(request.bookingId);
            ratingRequest.setRating(request.rating);
            ratingRequest.setComment(request.comment);

            RatingResponseDTO rating = ratingReviewService.createRating(user, ratingRequest);

            return ResponseEntity.ok(APIResponse.success("Rating created successfully", rating));
        } catch (Exception e) {
            System.err.println("ERROR in createRating: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(APIResponse.error("Error: " + e.getMessage()));
        }
    }
    
    // ✅ SIMPLE TEST - No parameters needed
    @GetMapping("/test")
    public ResponseEntity<APIResponse> testEndpoint() {
        return ResponseEntity.ok(APIResponse.success("Rating API is working!", null));
    }

    // Get all ratings for a maid
    @GetMapping("/maid/{maidId}")
    public ResponseEntity<APIResponse> getRatingsForMaid(@PathVariable Long maidId) {
        try {
            List<RatingResponseDTO> ratings = ratingReviewService.getRatingsForMaid(maidId);
            return ResponseEntity.ok(APIResponse.success("Ratings for maid", ratings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Error: " + e.getMessage()));
        }
    }

    // Get rating summary for maid
    @GetMapping("/maid/{maidId}/summary")
    public ResponseEntity<APIResponse> getMaidRatingSummary(@PathVariable Long maidId) {
        try {
            MaidRatingSummaryDTO summary = ratingReviewService.getMaidRatingSummary(maidId);
            return ResponseEntity.ok(APIResponse.success("Rating summary", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Error: " + e.getMessage()));
        }
    }

    // Get user history
    @GetMapping("/user/history")
    public ResponseEntity<APIResponse> getUserHistory(@RequestParam String mobile) {
        try {
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<UserHistoryDTO> history = ratingReviewService.getUserHistory(user);
            return ResponseEntity.ok(APIResponse.success("User history", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Error: " + e.getMessage()));
        }
    }

    // Get maid history
    @GetMapping("/maid/{maidId}/history")
    public ResponseEntity<APIResponse> getMaidHistory(@PathVariable Long maidId) {
        try {
            List<MaidHistoryDTO> history = ratingReviewService.getMaidHistory(maidId);
            return ResponseEntity.ok(APIResponse.success("Maid history", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Error: " + e.getMessage()));
        }
    }

    // Check if user can rate a booking
    @GetMapping("/can-rate")
    public ResponseEntity<APIResponse> canUserRateBooking(
            @RequestParam String mobile,
            @RequestParam Long bookingId) {
        try {
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean canRate = ratingReviewService.canUserRateBooking(user, bookingId);
            return ResponseEntity.ok(APIResponse.success("Can rate", canRate));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Error: " + e.getMessage()));
        }
    }

    // Request DTOs
    public static class TestCreateRequest {
        public Long bookingId;
        public Integer rating;
        public String comment;
    }

    public static class CreateRatingRequest {
        public String mobile;
        public Long bookingId;
        public Integer rating;
        public String comment;
    }
}