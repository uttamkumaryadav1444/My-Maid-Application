package MaidRepository.maid.dto;

import MaidRepository.maid.model.Booking.BookingStatus;

public class BookingStatusUpdateDTO {
    private BookingStatus status;
    private String notes;
    private String cancellationReason;

    public BookingStatusUpdateDTO() {}

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
}