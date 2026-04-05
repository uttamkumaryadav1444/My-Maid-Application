package MaidRepository.maid.dto;

import java.time.LocalDate;

public class LeaveRequestDTO {
    private Long maidId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;

    public LeaveRequestDTO() {}

    public LeaveRequestDTO(Long maidId, LocalDate startDate, LocalDate endDate, String reason) {
        this.maidId = maidId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    public Long getMaidId() { return maidId; }
    public void setMaidId(Long maidId) { this.maidId = maidId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}