package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.User;
import MaidRepository.maid.model.SubscriptionPlan;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.repository.SubscriptionPlanRepository;
import MaidRepository.maid.repository.SubscriptionDetailsRepository;
import MaidRepository.maid.service.RazorPayService;
import MaidRepository.maid.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private RazorPayService razorPayService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository planRepository;

    @Autowired
    private SubscriptionDetailsRepository detailsRepository;

    // ==================== PLAN MANAGEMENT ====================

    @GetMapping("/plans/all")
    public ResponseEntity<APIResponse> getAllPlans() {
        try {
            List<SubscriptionPlanDTO> plans = subscriptionService.getAllActivePlans();
            return ResponseEntity.ok(APIResponse.success("Subscription plans fetched successfully", plans));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch plans: " + e.getMessage()));
        }
    }

    @GetMapping("/plans/{planId}")
    public ResponseEntity<APIResponse> getPlanById(@PathVariable Long planId) {
        try {
            SubscriptionPlanDTO plan = subscriptionService.getPlanById(planId);
            return ResponseEntity.ok(APIResponse.success("Plan details fetched successfully", plan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch plan: " + e.getMessage()));
        }
    }

    @GetMapping("/plans/type/{type}")
    public ResponseEntity<APIResponse> getPlansByType(@PathVariable String type) {
        try {
            List<SubscriptionPlanDTO> plans = subscriptionService.getPlansByType(type);
            return ResponseEntity.ok(APIResponse.success("Plans fetched for type: " + type, plans));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch plans: " + e.getMessage()));
        }
    }

    @GetMapping("/plans/popular")
    public ResponseEntity<APIResponse> getPopularPlan() {
        try {
            SubscriptionPlanDTO plan = subscriptionService.getPopularPlan();
            return ResponseEntity.ok(APIResponse.success("Popular plan fetched", plan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch popular plan: " + e.getMessage()));
        }
    }

    @GetMapping("/plans/{planId}/savings")
    public ResponseEntity<APIResponse> calculateSavings(@PathVariable Long planId) {
        try {
            SubscriptionPlanDTO plan = subscriptionService.calculateSavings(planId);
            return ResponseEntity.ok(APIResponse.success("Savings calculated", plan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to calculate savings: " + e.getMessage()));
        }
    }

    // ==================== DISCOUNT & COUPON ====================

    @GetMapping("/user/subscriptionDiscount")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> getSubscriptionDetailsDiscount(@RequestParam("cuponCode") String cuponCode) {
        try {
            DiscountResponseDTO response = subscriptionService.applyDiscount(cuponCode);
            return ResponseEntity.ok(APIResponse.success(response.getMessage(), response.getSubscriptionDetails()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to apply discount: " + e.getMessage()));
        }
    }

    // ==================== CREATE RAZORPAY ORDER (NEW) ====================

    /**
     * Create RazorPay order for subscription payment
     */
    @PostMapping("/create-subscription-order")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> createSubscriptionOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SubscriptionOrderRequestDTO request) {

        try {
            log.info("Creating subscription order for plan: {}", request.getPlanType());

            // Extract mobile from token
            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid authorization header"));
            }

            // Get user
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + mobile));

            // Get plan details
            SubscriptionPlan plan = planRepository.findByPlanNameAndDurationDays(
                            request.getPlanType(), request.getDuration())
                    .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + request.getPlanType()));

            double amount = plan.getPrice();

            // Apply coupon if any
            if (request.getCuponCode() != null && !request.getCuponCode().isEmpty()) {
                try {
                    String validCoupon = detailsRepository.findCuponCode(request.getCuponCode());
                    if (validCoupon != null && validCoupon.equals(request.getCuponCode())) {
                        amount = amount * 0.90; // 10% discount
                        log.info("Coupon applied: {} - New amount: {}", request.getCuponCode(), amount);
                    }
                } catch (Exception e) {
                    log.warn("Invalid coupon code: {}", request.getCuponCode());
                }
            }

            // Create RazorPay order
            String orderId = razorPayService.createOrder(amount, "INR", "sub_" + System.currentTimeMillis());
            log.info("RazorPay order created: {}", orderId);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("amount", amount);
            response.put("currency", "INR");
            response.put("key", razorPayService.getRazorpayKeyId());
            response.put("planName", plan.getPlanName());
            response.put("duration", plan.getDurationDays());
            response.put("contactViews", plan.getContactViews());
            response.put("customerName", user.getFullName());
            response.put("customerEmail", user.getEmail());
            response.put("customerMobile", user.getMobile());

            return ResponseEntity.ok(APIResponse.success("Order created", response));

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Order creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Order creation failed: " + e.getMessage()));
        }
    }

    // ==================== SUBSCRIPTION ACTIVATION ====================

    /**
     * Activate subscription for user (WITH PAYMENT)
     */
    @PostMapping("/activate")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> activateSubscription(@RequestBody SubscriptionRequestDTO request) {
        try {
            log.info("Activating subscription for user: {}", request.getUserId());
            SubscriptionResponseDTO response = subscriptionService.activateSubscription(request);
            return ResponseEntity.ok(APIResponse.success("Subscription activated successfully", response));
        } catch (Exception e) {
            log.error("Activation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to activate subscription: " + e.getMessage()));
        }
    }

    /**
     * TEST MODE - Activate subscription without payment
     * Use this for testing purposes only
     */
    @PostMapping("/activate-test")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> activateSubscriptionTest(@RequestBody SubscriptionRequestDTO request) {
        try {
            log.info("🔧 TEST MODE: Activating subscription for user: {}", request.getUserId());

            // Set test payment method if not provided
            if (request.getPaymentMethod() == null) {
                request.setPaymentMethod("TEST");
            }
            if (request.getSubscriptionId() == null) {
                request.setSubscriptionId("TEST_SUB_" + System.currentTimeMillis());
            }

            SubscriptionResponseDTO response = subscriptionService.activateSubscription(request);

            log.info("✅ TEST subscription activated successfully for user: {}", request.getUserId());
            return ResponseEntity.ok(APIResponse.success("✅ Test subscription activated successfully", response));

        } catch (Exception e) {
            log.error("Test activation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Activation failed: " + e.getMessage()));
        }
    }

    // ==================== USER SUBSCRIPTION STATUS ====================

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> getActiveSubscription(@RequestHeader("userId") String userId) {
        try {
            SubscriptionResponseDTO subscription = subscriptionService.getUserActiveSubscription(userId);
            if (subscription != null) {
                return ResponseEntity.ok(APIResponse.success("Active subscription found", subscription));
            } else {
                return ResponseEntity.ok(APIResponse.success("No active subscription", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to get subscription: " + e.getMessage()));
        }
    }

    @GetMapping("/status")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> checkSubscriptionStatus(@RequestHeader("userId") String userId) {
        try {
            boolean hasActive = subscriptionService.hasActiveSubscription(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("hasActiveSubscription", hasActive);
            response.put("userId", userId);

            if (hasActive) {
                SubscriptionResponseDTO subscription = subscriptionService.getUserActiveSubscription(userId);
                response.put("subscription", subscription);
                response.put("remainingDays", subscriptionService.calculateRemainingDays(userId));
            }

            return ResponseEntity.ok(APIResponse.success("Subscription status fetched successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to check status: " + e.getMessage()));
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> getSubscriptionHistory(@RequestHeader("userId") String userId) {
        try {
            List<SubscriptionResponseDTO> history = subscriptionService.getUserSubscriptions(userId);
            return ResponseEntity.ok(APIResponse.success("Subscription history fetched successfully", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to get history: " + e.getMessage()));
        }
    }

    @GetMapping("/remaining-days")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> getRemainingDays(@RequestHeader("userId") String userId) {
        try {
            int remainingDays = subscriptionService.calculateRemainingDays(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("remainingDays", remainingDays);
            return ResponseEntity.ok(APIResponse.success("Remaining days calculated", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to calculate remaining days: " + e.getMessage()));
        }
    }

    // ==================== SUBSCRIPTION CANCELLATION ====================

    @PostMapping("/cancel")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> cancelSubscription(@RequestHeader("userId") String userId) {
        try {
            SubscriptionResponseDTO response = subscriptionService.cancelSubscription(userId);
            return ResponseEntity.ok(APIResponse.success("Subscription cancelled successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to cancel subscription: " + e.getMessage()));
        }
    }

    // ==================== CONTACT VIEW MANAGEMENT ====================

    @GetMapping("/can-view-contact")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> canViewContact(@RequestHeader("userId") String userId) {
        try {
            boolean canView = subscriptionService.canViewContact(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("canViewContact", canView);

            if (canView) {
                SubscriptionResponseDTO subscription = subscriptionService.getUserActiveSubscription(userId);
                response.put("remainingViews", subscription.getContactView());
            }

            return ResponseEntity.ok(APIResponse.success("Contact view permission checked", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to check permission: " + e.getMessage()));
        }
    }

    // ==================== HELPER METHODS ====================

    private String extractMobileFromAuthHeader(String authHeader) {
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
            return null;
        }
        return null;
    }
}