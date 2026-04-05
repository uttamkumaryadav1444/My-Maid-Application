package MaidRepository.maid.impl;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.*;
import MaidRepository.maid.repository.*;
import MaidRepository.maid.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionDetailsRepository detailsRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository planRepository;

    // ==================== PLAN MANAGEMENT ====================

    @Override
    public List<SubscriptionPlanDTO> getAllActivePlans() {
        List<SubscriptionPlan> plans = planRepository.findActivePlansOrderByPrice();
        return plans.stream()
                .map(this::convertToPlanDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionPlanDTO> getPlansByType(String type) {
        List<SubscriptionPlan> plans = planRepository.findByPlanType(type);
        if (plans == null) {
            return List.of();
        }
        return plans.stream()
                .map(this::convertToPlanDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlanDTO getPlanById(Long planId) {
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + planId));
        return convertToPlanDTO(plan);
    }

    @Override
    public SubscriptionPlanDTO getPopularPlan() {
        List<SubscriptionPlan> plans = planRepository.findActivePlansOrderByPrice();
        if (plans.isEmpty()) {
            throw new IllegalArgumentException("No plans available");
        }

        SubscriptionPlan popular = plans.stream()
                .filter(p -> "Silver".equals(p.getPlanName()))
                .findFirst()
                .orElse(plans.get(plans.size() - 1));

        return convertToPlanDTO(popular);
    }

    @Override
    public SubscriptionPlanDTO calculateSavings(Long planId) {
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        return convertToPlanDTO(plan);
    }

    // ==================== DISCOUNT & COUPON ====================

    @Override
    public DiscountResponseDTO applyDiscount(String cuponCode) {
        DiscountResponseDTO response = new DiscountResponseDTO();

        List<SubscriptionDetails> details = detailsRepository.findDetails();
        if (details == null) {
            details = List.of();
        }

        String fetchedCupon = null;
        try {
            fetchedCupon = detailsRepository.findCuponCode(cuponCode);
        } catch (Exception e) {
            System.out.println("Error fetching coupon: " + e.getMessage());
        }

        System.out.println("Fetched coupon: " + fetchedCupon);

        boolean isCuponValid = fetchedCupon != null && fetchedCupon.equals(cuponCode);

        for (SubscriptionDetails subscription : details) {
            Integer amount = subscription.getAmount();
            int payment = (amount != null) ? amount : 0;

            if (isCuponValid) {
                int discountedAmount = (int) (payment * 0.90);
                subscription.setDiscountAmount(discountedAmount);
            } else {
                subscription.setDiscountAmount(payment);
            }
        }

        response.setMessage(isCuponValid ? "Discount applied successfully" : "Invalid referral code, no discount applied");

        List<SubscriptionPlanDTO> planDTOs = details.stream()
                .map(this::convertDetailsToPlanDTO)
                .collect(Collectors.toList());

        response.setSubscriptionDetails(planDTOs);

        return response;
    }

    // ==================== SUBSCRIPTION ACTIVATION ====================

    @Override
    public SubscriptionResponseDTO activateSubscription(SubscriptionRequestDTO request) {
        if (request == null || request.getUserId() == null) {
            throw new IllegalArgumentException("Invalid subscription request");
        }

        User user = userRepository.findByMobile(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + request.getUserId()));

        String planType = request.getType() != null ? request.getType() : "BASIC";
        Integer duration = request.getDuration() != null ? request.getDuration() : 30;

        SubscriptionPlan plan = planRepository.findByPlanNameAndDurationDays(planType, duration)
                .orElseThrow(() -> new IllegalArgumentException("Invalid plan: " + planType +
                        " with duration " + duration + " days"));

        LocalDate startDate = LocalDate.now();
        Integer planDuration = plan.getDurationDays();
        if (planDuration == null) {
            planDuration = 30;
        }
        LocalDate endDate = startDate.plusDays(planDuration);

        Double planPrice = plan.getPrice();
        int finalPrice = (planPrice != null) ? planPrice.intValue() : 199;

        if (request.getCuponCode() != null && !request.getCuponCode().isEmpty()) {
            try {
                String validCupon = detailsRepository.findCuponCode(request.getCuponCode());
                if (validCupon != null && validCupon.equals(request.getCuponCode())) {
                    finalPrice = (int) (finalPrice * 0.90);
                }
            } catch (Exception e) {
                System.out.println("Error applying coupon: " + e.getMessage());
            }
        }

        Subscriber subscriber = new Subscriber();
        subscriber.setUserId(user.getMobile());
        subscriber.setPrice(finalPrice);

        Integer contactViews = plan.getContactViews();
        subscriber.setContactView(contactViews != null ? contactViews.longValue() : 10L);

        subscriber.setType(planType);
        subscriber.setDuration(planDuration);
        subscriber.setStartDate(startDate);
        subscriber.setEndDate(endDate);
        subscriber.setStartTime(LocalTime.now());
        subscriber.setEndTime(LocalTime.now());
        subscriber.setEventType("SUBSCRIPTION");
        subscriber.setSubscriptionId(request.getSubscriptionId() != null ?
                request.getSubscriptionId() : "SUB_" + UUID.randomUUID().toString());
        subscriber.setCuponCode(request.getCuponCode());
        subscriber.setRemainsDays(planDuration);

        String features = plan.getFeatures();
        subscriber.setDescription(features != null ? features : "Standard features");

        subscriber.setPlanType(planType);
        subscriber.setStatus("ACTIVE");

        Subscriber saved = subscriberRepository.save(subscriber);

        user.setSubscriptionPlan(planType);
        user.setSubscriptionExpiry(endDate.atStartOfDay());
        user.setSubscriptionActivated(true);
        user.setIsSubscribed(true);
        userRepository.save(user);

        return convertToResponseDTO(saved);
    }

    // ==================== USER SUBSCRIPTION STATUS ====================

    @Override
    public SubscriptionResponseDTO getUserActiveSubscription(String userId) {
        List<Subscriber> active = subscriberRepository.findActiveSubscriptions(userId);

        if (active == null || active.isEmpty()) {
            return null;
        }

        Subscriber latest = active.get(0);

        if (latest.getEndDate() != null) {
            long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), latest.getEndDate());
            latest.setRemainsDays((int) Math.max(0, remainingDays));
        }

        return convertToResponseDTO(latest);
    }

    @Override
    public boolean hasActiveSubscription(String userId) {
        try {
            return subscriberRepository.existsByUserIdAndEndDateGreaterThanEqual(userId, LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<SubscriptionResponseDTO> getUserSubscriptions(String userId) {
        List<Subscriber> subscribers = subscriberRepository.findByUserId(userId);
        if (subscribers == null) {
            return List.of();
        }
        return subscribers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public int calculateRemainingDays(String userId) {
        List<Subscriber> active = subscriberRepository.findActiveSubscriptions(userId);

        if (active == null || active.isEmpty()) {
            return 0;
        }

        Subscriber latest = active.get(0);
        if (latest.getEndDate() != null) {
            return (int) Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), latest.getEndDate()));
        }
        return 0;
    }

    // ==================== SUBSCRIPTION CANCELLATION ====================

    @Override
    public SubscriptionResponseDTO cancelSubscription(String userId) {
        List<Subscriber> active = subscriberRepository.findActiveSubscriptions(userId);

        if (active == null || active.isEmpty()) {
            throw new IllegalArgumentException("No active subscription found for user: " + userId);
        }

        Subscriber subscription = active.get(0);
        subscription.setEndDate(LocalDate.now().minusDays(1));
        subscription.setStatus("CANCELLED");

        Subscriber cancelled = subscriberRepository.save(subscription);

        User user = userRepository.findByMobile(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setSubscriptionActivated(false);
        user.setSubscriptionPlan("FREE");
        user.setIsSubscribed(false);
        userRepository.save(user);

        return convertToResponseDTO(cancelled);
    }

    // ==================== CONTACT VIEW MANAGEMENT ====================

    @Override
    public boolean canViewContact(String userId) {
        List<Subscriber> active = subscriberRepository.findActiveSubscriptions(userId);

        if (active == null || active.isEmpty()) {
            return false;
        }

        Subscriber subscription = active.get(0);
        Long contactView = subscription.getContactView();

        return contactView != null && contactView > 0;
    }

    @Override
    public void decrementContactView(String userId) {
        List<Subscriber> active = subscriberRepository.findActiveSubscriptions(userId);

        if (active != null && !active.isEmpty()) {
            Subscriber subscription = active.get(0);
            Long currentViews = subscription.getContactView();
            if (currentViews != null && currentViews > 0) {
                subscription.setContactView(currentViews - 1);
                subscriberRepository.save(subscription);
            }
        }
    }

    // ==================== CONVERTERS ====================

    private SubscriptionPlanDTO convertToPlanDTO(SubscriptionPlan plan) {
        SubscriptionPlanDTO dto = new SubscriptionPlanDTO();
        dto.setId(plan.getId());
        dto.setPlanName(plan.getPlanName());
        dto.setPlanType(plan.getPlanType());
        dto.setPrice(plan.getPrice());
        dto.setDurationDays(plan.getDurationDays());
        dto.setContactViews(plan.getContactViews());

        if (plan.getFeatures() != null && !plan.getFeatures().isEmpty()) {
            dto.setFeatures(Arrays.asList(plan.getFeatures().split(", ")));
        } else {
            dto.setFeatures(List.of());
        }

        dto.setIsActive(plan.getIsActive());
        dto.setSortOrder(plan.getSortOrder());
        return dto;
    }

    private SubscriptionPlanDTO convertDetailsToPlanDTO(SubscriptionDetails details) {
        SubscriptionPlanDTO dto = new SubscriptionPlanDTO();

        // ✅ FIXED: int cannot be null, so check for 0 (default)
        int id = details.getId();
        if (id != 0) {
            dto.setId((long) id);
        }

        String type = details.getType();
        dto.setPlanName(type != null ? type : "Standard");
        dto.setPlanType("Subscription");

        Integer amount = details.getAmount();
        dto.setPrice(amount != null ? amount.doubleValue() : 0.0);

        Integer duration = details.getDuration();
        dto.setDurationDays(duration != null ? duration : 30);

        String contactView = details.getContactView();
        if (contactView != null) {
            try {
                dto.setContactViews(Integer.parseInt(contactView));
            } catch (NumberFormatException e) {
                dto.setContactViews(10);
            }
        } else {
            dto.setContactViews(10);
        }

        String description = details.getDescription();
        if (description != null && !description.isEmpty()) {
            dto.setFeatures(Arrays.asList(description.split(", ")));
        } else {
            dto.setFeatures(List.of());
        }

        dto.setDiscountAmount(details.getDiscountAmount());
        return dto;
    }

    private SubscriptionResponseDTO convertToResponseDTO(Subscriber subscriber) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(subscriber.getId());
        dto.setUserId(subscriber.getUserId());
        dto.setPrice(subscriber.getPrice());
        dto.setContactView(subscriber.getContactView());
        dto.setType(subscriber.getType());
        dto.setDuration(subscriber.getDuration());
        dto.setStartDate(subscriber.getStartDate());
        dto.setEndDate(subscriber.getEndDate());
        dto.setStartTime(subscriber.getStartTime());
        dto.setEndTime(subscriber.getEndTime());
        dto.setEventType(subscriber.getEventType());
        dto.setSubscriptionId(subscriber.getSubscriptionId());
        dto.setCuponCode(subscriber.getCuponCode());
        dto.setRemainsDays(subscriber.getRemainsDays());
        dto.setDescription(subscriber.getDescription());
        dto.setPlanType(subscriber.getPlanType());

        boolean isActive = subscriber.getEndDate() != null &&
                subscriber.getEndDate().isAfter(LocalDate.now()) &&
                !"CANCELLED".equals(subscriber.getStatus());
        dto.setActive(isActive);

        dto.setCreatedAt(subscriber.getCreatedAt());
        dto.setUpdatedAt(subscriber.getUpdatedAt());
        return dto;
    }
}