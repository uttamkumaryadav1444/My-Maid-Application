package MaidRepository.maid.service;

import MaidRepository.maid.dto.*;
import java.util.List;

public interface SubscriptionService {

    // ==================== PLAN MANAGEMENT ====================
    List<SubscriptionPlanDTO> getAllActivePlans();
    List<SubscriptionPlanDTO> getPlansByType(String type);
    SubscriptionPlanDTO getPlanById(Long planId);
    SubscriptionPlanDTO getPopularPlan();
    SubscriptionPlanDTO calculateSavings(Long planId);

    // ==================== DISCOUNT & COUPON ====================
    DiscountResponseDTO applyDiscount(String cuponCode);

    // ==================== SUBSCRIPTION ACTIVATION ====================
    SubscriptionResponseDTO activateSubscription(SubscriptionRequestDTO request);

    // ==================== USER SUBSCRIPTION STATUS ====================
    SubscriptionResponseDTO getUserActiveSubscription(String userId);
    boolean hasActiveSubscription(String userId);
    List<SubscriptionResponseDTO> getUserSubscriptions(String userId);
    int calculateRemainingDays(String userId);

    // ==================== SUBSCRIPTION CANCELLATION ====================
    SubscriptionResponseDTO cancelSubscription(String userId);

    // ==================== CONTACT VIEW MANAGEMENT ====================
    boolean canViewContact(String userId);
    void decrementContactView(String userId);
}