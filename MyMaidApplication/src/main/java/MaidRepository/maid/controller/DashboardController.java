package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.dto.RatingReviewDTO.*;
import MaidRepository.maid.model.Booking.BookingStatus;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.BookingService;
import MaidRepository.maid.service.MaidService;
import MaidRepository.maid.service.RatingReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final MaidService maidService;
    private final BookingService bookingService;
    private final RatingReviewService ratingReviewService;
    private final MaidRepository maidRepository;
    private final UserRepository userRepository;

    public DashboardController(MaidService maidService,
                               BookingService bookingService,
                               RatingReviewService ratingReviewService,
                               MaidRepository maidRepository,
                               UserRepository userRepository) {
        this.maidService = maidService;
        this.bookingService = bookingService;
        this.ratingReviewService = ratingReviewService;
        this.maidRepository = maidRepository;
        this.userRepository = userRepository;
    }

    // ✅ Get maids for user dashboard (existing method - keep as is)
    @GetMapping("/user/maids")
    public ResponseEntity<APIResponse> getUserDashboardMaids(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Double maxHourlyRate,
            @RequestParam(required = false) String skill,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "hourlyRate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            // ✅ advancedSearch method call fix करें:
            List<MaidResponseDTO> maids = maidService.advancedSearch(
                    city, serviceType, minExperience, maxHourlyRate, true);

            // If skill filter is provided
            if (skill != null && !skill.isEmpty()) {
                maids = maids.stream()
                        .filter(maid -> maid.getSkills() != null &&
                                maid.getSkills().toLowerCase().contains(skill.toLowerCase()))
                        .toList();
            }

            // ✅ sortMaids method call fix करें:
            maids = maidService.sortMaids(sortBy, direction);

            // Paginate manually
            int start = Math.min(page * size, maids.size());
            int end = Math.min((page + 1) * size, maids.size());
            List<MaidResponseDTO> paginatedMaids = maids.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("maids", paginatedMaids);
            response.put("total", maids.size());
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", (int) Math.ceil((double) maids.size() / size));

            return ResponseEntity.ok(APIResponse.success("Maids fetched for dashboard", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maids: " + e.getMessage()));
        }
    }

    // ✅ Get user stats with history and rating (Updated)
    @GetMapping("/user/stats")
    public ResponseEntity<APIResponse> getUserDashboardStats(@RequestParam String mobile) {
        try {
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Map<String, Object> stats = new HashMap<>();

            // Get user bookings count by status
            List<BookingResponseDTO> allBookings = bookingService.getUserBookings(user);

            long pendingCount = allBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.PENDING).count();
            long acceptedCount = allBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.ACCEPTED).count();
            long completedCount = allBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();

            // Get user history with ratings
            List<UserHistoryDTO> history = ratingReviewService.getUserHistory(user);

            // Calculate total spent from completed bookings
            Double totalSpent = history.stream()
                    .filter(h -> h.getTotalAmount() != null && h.getStatus().equals("COMPLETED"))
                    .mapToDouble(UserHistoryDTO::getTotalAmount)
                    .sum();

            // Calculate average rating given by user
            Double userAverageRating = history.stream()
                    .filter(h -> h.getRating() != null)
                    .mapToDouble(UserHistoryDTO::getRating)
                    .average()
                    .orElse(0.0);

            // Count of bookings that can be rated
            long canRateCount = history.stream()
                    .filter(UserHistoryDTO::isCanRate)
                    .count();

            stats.put("totalBookings", allBookings.size());
            stats.put("pendingBookings", pendingCount);
            stats.put("acceptedBookings", acceptedCount);
            stats.put("completedBookings", completedCount);
            stats.put("userName", user.getFullName());
            stats.put("userEmail", user.getEmail());
            stats.put("userMobile", user.getMobile());
            stats.put("history", history);
            stats.put("totalSpent", totalSpent);
            stats.put("profilePhoto", user.getProfilePhotoUrl());
            stats.put("isVerified", user.getIsVerified());
            stats.put("userAverageRating", Math.round(userAverageRating * 10.0) / 10.0);
            stats.put("canRateCount", canRateCount);
            stats.put("historyCount", history.size());

            return ResponseEntity.ok(APIResponse.success("User dashboard stats with history", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch stats: " + e.getMessage()));
        }
    }

    // ✅ Get maid stats with history and rating (Updated)
    @GetMapping("/maid/{maidId}/stats")
    public ResponseEntity<APIResponse> getMaidDashboardStats(@PathVariable Long maidId) {
        try {
            Maid maid = maidRepository.findById(maidId)
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            Map<String, Object> stats = new HashMap<>();

            // Get maid bookings count by status
            List<BookingResponseDTO> allBookings = bookingService.getMaidBookings(maidId);

            long pendingCount = allBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.PENDING).count();
            long acceptedCount = allBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.ACCEPTED).count();
            long completedCount = allBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();

            // Get maid history with ratings
            List<MaidHistoryDTO> history = ratingReviewService.getMaidHistory(maidId);

            // Get rating summary
            MaidRatingSummaryDTO ratingSummary = ratingReviewService.getMaidRatingSummary(maidId);

            // Calculate total earnings from completed bookings
            Double totalEarnings = history.stream()
                    .filter(h -> h.getStatus().equals("COMPLETED") && h.getTotalAmount() != null)
                    .mapToDouble(MaidHistoryDTO::getTotalAmount)
                    .sum();

            // Calculate average service hours (assuming 1 hour per booking, can be adjusted)
            long totalServiceHours = history.stream()
                    .filter(h -> h.getStatus().equals("COMPLETED"))
                    .count();

            // Get recent bookings (last 5)
            List<MaidHistoryDTO> recentBookings = history.stream()
                    .limit(5)
                    .toList();

            // Get bookings with ratings
            List<MaidHistoryDTO> ratedBookings = history.stream()
                    .filter(h -> h.getRating() != null)
                    .toList();

            stats.put("maidName", maid.getName());
            stats.put("maidMobile", maid.getMobile());
            stats.put("maidEmail", maid.getEmail());
            stats.put("serviceType", maid.getServiceType());
            stats.put("hourlyRate", maid.getHourlyRate());
            stats.put("isAvailable", maid.getIsAvailable());
            stats.put("isVerified", maid.getIsVerified());
            stats.put("experience", maid.getExperience());
            stats.put("skills", maid.getSkills());
            stats.put("city", maid.getCity());
            stats.put("totalBookings", allBookings.size());
            stats.put("pendingBookings", pendingCount);
            stats.put("acceptedBookings", acceptedCount);
            stats.put("completedBookings", completedCount);
            stats.put("todayBookings", bookingService.getTodayBookingsForMaid(maidId).size());
            stats.put("history", history);
            stats.put("ratingSummary", ratingSummary);
            stats.put("totalEarnings", totalEarnings);
            stats.put("profilePhoto", maid.getProfilePhotoUrl());
            stats.put("averageRating", maid.getAverageRating());
            stats.put("totalServiceHours", totalServiceHours);
            stats.put("recentBookings", recentBookings);
            stats.put("ratedBookingsCount", ratedBookings.size());
            stats.put("historyCount", history.size());

            return ResponseEntity.ok(APIResponse.success("Maid dashboard stats with history", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maid stats: " + e.getMessage()));
        }
    }

    // ✅ NEW: Get user detailed history with filters
    @GetMapping("/user/history/detailed")
    public ResponseEntity<APIResponse> getUserDetailedHistory(
            @RequestParam String mobile,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean hasRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<UserHistoryDTO> history = ratingReviewService.getUserHistory(user);

            // Apply filters if provided
            if (status != null && !status.isEmpty()) {
                history = history.stream()
                        .filter(h -> h.getStatus().equalsIgnoreCase(status))
                        .toList();
            }

            if (hasRating != null) {
                if (hasRating) {
                    history = history.stream()
                            .filter(h -> h.getRating() != null)
                            .toList();
                } else {
                    history = history.stream()
                            .filter(h -> h.getRating() == null)
                            .toList();
                }
            }

            // Pagination
            int start = Math.min(page * size, history.size());
            int end = Math.min((page + 1) * size, history.size());
            List<UserHistoryDTO> paginatedHistory = history.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("history", paginatedHistory);
            response.put("total", history.size());
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", (int) Math.ceil((double) history.size() / size));

            return ResponseEntity.ok(APIResponse.success("User detailed history", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch detailed history: " + e.getMessage()));
        }
    }

    // ✅ NEW: Get maid detailed history with filters
    @GetMapping("/maid/{maidId}/history/detailed")
    public ResponseEntity<APIResponse> getMaidDetailedHistory(
            @PathVariable Long maidId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean hasRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<MaidHistoryDTO> history = ratingReviewService.getMaidHistory(maidId);

            // Apply filters if provided
            if (status != null && !status.isEmpty()) {
                history = history.stream()
                        .filter(h -> h.getStatus().equalsIgnoreCase(status))
                        .toList();
            }

            if (hasRating != null) {
                if (hasRating) {
                    history = history.stream()
                            .filter(h -> h.getRating() != null)
                            .toList();
                } else {
                    history = history.stream()
                            .filter(h -> h.getRating() == null)
                            .toList();
                }
            }

            // Pagination
            int start = Math.min(page * size, history.size());
            int end = Math.min((page + 1) * size, history.size());
            List<MaidHistoryDTO> paginatedHistory = history.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("history", paginatedHistory);
            response.put("total", history.size());
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", (int) Math.ceil((double) history.size() / size));

            return ResponseEntity.ok(APIResponse.success("Maid detailed history", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maid detailed history: " + e.getMessage()));
        }
    }

    // ✅ NEW: Get user statistics summary
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<APIResponse> getUserSummary(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Map<String, Object> summary = new HashMap<>();

            List<BookingResponseDTO> allBookings = bookingService.getUserBookings(user);
            List<UserHistoryDTO> history = ratingReviewService.getUserHistory(user);

            // Calculate various statistics
            long totalBookings = allBookings.size();
            long completedBookings = history.stream()
                    .filter(h -> h.getStatus().equals("COMPLETED"))
                    .count();
            long pendingBookings = history.stream()
                    .filter(h -> h.getStatus().equals("PENDING"))
                    .count();
            long acceptedBookings = history.stream()
                    .filter(h -> h.getStatus().equals("ACCEPTED"))
                    .count();

            Double totalSpent = history.stream()
                    .filter(h -> h.getTotalAmount() != null && h.getStatus().equals("COMPLETED"))
                    .mapToDouble(UserHistoryDTO::getTotalAmount)
                    .sum();

            long ratedBookings = history.stream()
                    .filter(h -> h.getRating() != null)
                    .count();

            Double averageRatingGiven = history.stream()
                    .filter(h -> h.getRating() != null)
                    .mapToDouble(UserHistoryDTO::getRating)
                    .average()
                    .orElse(0.0);

            summary.put("userId", user.getId());
            summary.put("userName", user.getFullName());
            summary.put("totalBookings", totalBookings);
            summary.put("completedBookings", completedBookings);
            summary.put("pendingBookings", pendingBookings);
            summary.put("acceptedBookings", acceptedBookings);
            summary.put("totalSpent", totalSpent);
            summary.put("ratedBookings", ratedBookings);
            summary.put("averageRatingGiven", Math.round(averageRatingGiven * 10.0) / 10.0);
            summary.put("bookingCompletionRate", totalBookings > 0 ?
                    Math.round((completedBookings * 100.0) / totalBookings) : 0);

            return ResponseEntity.ok(APIResponse.success("User summary statistics", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch user summary: " + e.getMessage()));
        }
    }

    // ✅ NEW: Get maid statistics summary
    @GetMapping("/maid/{maidId}/summary")
    public ResponseEntity<APIResponse> getMaidSummary(@PathVariable Long maidId) {
        try {
            Maid maid = maidRepository.findById(maidId)
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            Map<String, Object> summary = new HashMap<>();

            List<BookingResponseDTO> allBookings = bookingService.getMaidBookings(maidId);
            List<MaidHistoryDTO> history = ratingReviewService.getMaidHistory(maidId);
            MaidRatingSummaryDTO ratingSummary = ratingReviewService.getMaidRatingSummary(maidId);

            // Calculate various statistics
            long totalBookings = allBookings.size();
            long completedBookings = history.stream()
                    .filter(h -> h.getStatus().equals("COMPLETED"))
                    .count();
            long pendingBookings = history.stream()
                    .filter(h -> h.getStatus().equals("PENDING"))
                    .count();
            long acceptedBookings = history.stream()
                    .filter(h -> h.getStatus().equals("ACCEPTED"))
                    .count();

            Double totalEarnings = history.stream()
                    .filter(h -> h.getStatus().equals("COMPLETED") && h.getTotalAmount() != null)
                    .mapToDouble(MaidHistoryDTO::getTotalAmount)
                    .sum();

            long ratedBookings = history.stream()
                    .filter(h -> h.getRating() != null)
                    .count();

            Double averageRatingReceived = history.stream()
                    .filter(h -> h.getRating() != null)
                    .mapToDouble(MaidHistoryDTO::getRating)
                    .average()
                    .orElse(0.0);

            summary.put("maidId", maid.getId());
            summary.put("maidName", maid.getName());
            summary.put("serviceType", maid.getServiceType());
            summary.put("hourlyRate", maid.getHourlyRate());
            summary.put("totalBookings", totalBookings);
            summary.put("completedBookings", completedBookings);
            summary.put("pendingBookings", pendingBookings);
            summary.put("acceptedBookings", acceptedBookings);
            summary.put("totalEarnings", totalEarnings);
            summary.put("ratedBookings", ratedBookings);
            summary.put("averageRatingReceived", Math.round(averageRatingReceived * 10.0) / 10.0);
            summary.put("bookingCompletionRate", totalBookings > 0 ?
                    Math.round((completedBookings * 100.0) / totalBookings) : 0);
            summary.put("ratingSummary", ratingSummary);
            summary.put("isAvailable", maid.getIsAvailable());
            summary.put("experience", maid.getExperience());

            return ResponseEntity.ok(APIResponse.success("Maid summary statistics", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maid summary: " + e.getMessage()));
        }
    }
}