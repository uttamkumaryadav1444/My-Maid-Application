package MaidRepository.maid.dto;

import lombok.Data;
import java.util.List;

@Data
public class DiscountResponseDTO {
    private String message;
    private List<SubscriptionPlanDTO> subscriptionDetails;

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<SubscriptionPlanDTO> getSubscriptionDetails() { return subscriptionDetails; }
    public void setSubscriptionDetails(List<SubscriptionPlanDTO> subscriptionDetails) {
        this.subscriptionDetails = subscriptionDetails;
    }
}