package MaidRepository.maid.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "subscriptionDetails")
public class SubscriptionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int amount;

    @Column(name = "contactView")
    private String contactView;

    private String type;
    private int duration;

    @Column(name = "discountAmount")
    private Integer discountAmount = 0;

    private String description;

    @Column(name = "remainingDays")
    private String remainingDays;

    @Column(name = "activePlaneName")
    private String activePlaneName;
}