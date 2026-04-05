package MaidRepository.maid.repository;

import MaidRepository.maid.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    List<SubscriptionPlan> findByIsActiveTrueOrderBySortOrderAsc();

    List<SubscriptionPlan> findByPlanType(String planType);

    // ✅ FIXED: Method name matches entity field
    Optional<SubscriptionPlan> findByPlanNameAndDurationDays(String planName, Integer durationDays);

    @Query("SELECT p FROM SubscriptionPlan p WHERE p.isActive = true ORDER BY p.price ASC")
    List<SubscriptionPlan> findActivePlansOrderByPrice();

    @Query("SELECT p FROM SubscriptionPlan p WHERE p.durationDays = :days")
    List<SubscriptionPlan> findByDurationDays(@Param("days") Integer days);

    @Query("SELECT p FROM SubscriptionPlan p WHERE p.contactViews >= :minViews")
    List<SubscriptionPlan> findByMinContactViews(@Param("minViews") Integer minViews);
}