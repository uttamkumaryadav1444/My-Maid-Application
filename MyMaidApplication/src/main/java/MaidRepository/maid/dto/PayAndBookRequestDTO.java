package MaidRepository.maid.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PayAndBookRequestDTO {
    private Long maidId;
    private String bookingType;  // Keep as String, will convert to enum
    private Integer hours;
    private Integer days;
    private Integer weeks;
    private Integer months;
    private String orderId;
    private String paymentId;
    private String signature;
    private LocalDate serviceDate;
    private LocalTime startTime;
    private Double totalAmount;
    private String address;
    private String specialInstructions;
}