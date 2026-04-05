package MaidRepository.maid.dto;

import MaidRepository.maid.model.Booking.BookingStatus;
import lombok.Data;

@Data
public class BookingStatusUpdateDTO {
    private BookingStatus status;
    private String notes;
    private String cancellationReason;
}