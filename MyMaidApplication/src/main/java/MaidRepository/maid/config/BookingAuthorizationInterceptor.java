package MaidRepository.maid.config;

import MaidRepository.maid.model.Subscriber;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.SubscriberRepository;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class BookingAuthorizationInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(BookingAuthorizationInterceptor.class);

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // ✅ Skip for OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // ✅ Skip for non-POST methods or non-booking endpoints
        if (!request.getMethod().equals("POST") ||
                !request.getRequestURI().contains("/api/bookings/")) {
            return true;
        }

        String userIdStr = null;

        // ✅ FIRST: Extract userId from Authorization token
        String authHeader = request.getHeader("Authorization");
        log.debug("Auth Header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (token.startsWith("token-")) {
                String[] parts = token.split("-");
                if (parts.length >= 2) {
                    userIdStr = parts[1];  // This is the mobile number
                    log.debug("Extracted mobile from token: {}", userIdStr);
                }
            }
        }

        // ✅ SECOND: If not found, try header
        if (userIdStr == null || userIdStr.isEmpty()) {
            userIdStr = request.getHeader("userId");
            log.debug("Received userId header: {}", userIdStr);
        }

        log.info("===== INTERCEPTOR =====");
        log.info("Processing booking request for userId: {}", userIdStr);

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            sendError(response, "User ID required");
            return false;
        }

        // ✅ CHECK SUBSCRIPTION
        boolean hasActive = false;
        String mobileForLog = "";

        // 1. TRY AS MOBILE NUMBER DIRECTLY
        log.info("Checking subscription for mobile: {}", userIdStr);
        List<Subscriber> subs = subscriberRepository.findByUserId(userIdStr);
        log.info("Found {} subscribers for mobile {}", subs.size(), userIdStr);

        for (Subscriber sub : subs) {
            log.debug("Subscriber: status={}, endDate={}", sub.getStatus(), sub.getEndDate());

            if ("ACTIVE".equals(sub.getStatus()) &&
                    sub.getEndDate() != null &&
                    sub.getEndDate().isAfter(LocalDate.now())) {
                hasActive = true;
                mobileForLog = userIdStr;
                log.info("✅ Active subscription found for mobile: {}", userIdStr);
                break;
            }
        }

        // 2. IF NOT FOUND, TRY AS DATABASE ID
        if (!hasActive) {
            try {
                Long dbId = Long.parseLong(userIdStr);
                log.info("Trying as database ID: {}", dbId);

                Optional<User> userOpt = userRepository.findById(dbId);
                if (userOpt.isPresent()) {
                    String mobile = userOpt.get().getMobile();
                    log.info("Found user with mobile: {}", mobile);

                    subs = subscriberRepository.findByUserId(mobile);
                    log.info("Found {} subscribers for mobile {}", subs.size(), mobile);

                    for (Subscriber sub : subs) {
                        if ("ACTIVE".equals(sub.getStatus()) &&
                                sub.getEndDate() != null &&
                                sub.getEndDate().isAfter(LocalDate.now())) {
                            hasActive = true;
                            mobileForLog = mobile;
                            log.info("✅ Active subscription found for DB ID {} (mobile: {})", dbId, mobile);
                            break;
                        }
                    }
                } else {
                    log.info("No user found with ID: {}", dbId);
                }
            } catch (NumberFormatException e) {
                log.debug("Not a number, skipping database ID check");
            }
        }

        // 3. FOR TESTING - BYPASS SUBSCRIPTION CHECK FOR CREATE-ORDER
        // ✅ TEMPORARY FIX: Allow create-order even without subscription for testing
        if (!hasActive && request.getRequestURI().contains("/create-order")) {
            log.info("⚠️ No subscription found but allowing create-order for testing");
            log.info("✅ Proceeding with booking (test mode)");
            return true;
        }

        if (!hasActive) {
            log.warn("❌ NO ACTIVE SUBSCRIPTION FOUND for userId: {}", userIdStr);
            sendError(response, "Active subscription required to book maids. Please subscribe.");
            return false;
        }

        log.info("✅ Subscription verified for user, proceeding with booking");
        return true;
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(403);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
    }
}