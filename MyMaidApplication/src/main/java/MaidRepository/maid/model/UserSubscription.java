package MaidRepository.maid.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;

    @Column(name = "subscription_id")
    private String subscriptionId;      // From payment gateway

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "price_paid")
    private Double pricePaid;

    @Column(name = "contact_views_remaining")
    private Integer contactViewsRemaining;

    @Column(name = "cupon_code")
    private String cuponCode;

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Column(name = "status")
    private String status;              // ACTIVE, EXPIRED, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return "ACTIVE".equals(status) &&
                endDate != null &&
                endDate.isAfter(LocalDateTime.now());
    }
}