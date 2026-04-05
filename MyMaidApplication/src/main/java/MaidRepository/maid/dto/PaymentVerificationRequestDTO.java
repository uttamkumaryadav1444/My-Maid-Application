package MaidRepository.maid.dto;

import lombok.Data;

@Data
public class PaymentVerificationRequestDTO {
    private String orderId;
    private String paymentId;
    private String signature;
    private Long bookingId;
}