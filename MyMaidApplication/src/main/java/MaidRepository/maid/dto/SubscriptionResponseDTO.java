package MaidRepository.maid.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class SubscriptionResponseDTO {
    private Long id;
    private String userId;
    private Integer price;
    private Long contactView;
    private String type;
    private Integer duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String eventType;
    private String subscriptionId;
    private String cuponCode;
    private Integer remainsDays;
    private String description;
    private String planType;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private Double amount;
}