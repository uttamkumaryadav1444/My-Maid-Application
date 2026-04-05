package MaidRepository.maid.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubscriptionPlanDTO {
    private Long id;
    private String planName;
    private String planType;
    private Double price;
    private Integer durationDays;
    private Integer contactViews;
    private List<String> features;
    private Boolean isActive;
    private Integer sortOrder;
    private String badge;
    private Double savings;
    private Integer discountAmount;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public Integer getContactViews() { return contactViews; }
    public void setContactViews(Integer contactViews) { this.contactViews = contactViews; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public Double getSavings() { return savings; }
    public void setSavings(Double savings) { this.savings = savings; }

    public Integer getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Integer discountAmount) { this.discountAmount = discountAmount; }
}