package MaidRepository.maid.dto;

import lombok.Data;

@Data
public class SubscriptionRequestDTO {
    private String userId;
    private String type;           // BASIC, BRONZE, SILVER, GOLD
    private Integer duration;       // 30, 60, 90, 120
    private String cuponCode;
    private String paymentMethod;
    private String subscriptionId;
    private Double amount;
}