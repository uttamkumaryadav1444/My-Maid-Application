package MaidRepository.maid.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MaidLeaveDTO {
    private Long id;
    private Long maidId;
    private String maidName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private Long replacementMaidId;
    private String replacementMaidName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer leaveDays;

    public MaidLeaveDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMaidId() { return maidId; }
    public void setMaidId(Long maidId) { this.maidId = maidId; }
    public String getMaidName() { return maidName; }
    public void setMaidName(String maidName) { this.maidName = maidName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getReplacementMaidId() { return replacementMaidId; }
    public void setReplacementMaidId(Long replacementMaidId) { this.replacementMaidId = replacementMaidId; }
    public String getReplacementMaidName() { return replacementMaidName; }
    public void setReplacementMaidName(String replacementMaidName) { this.replacementMaidName = replacementMaidName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getLeaveDays() { return leaveDays; }
    public void setLeaveDays(Integer leaveDays) { this.leaveDays = leaveDays; }
}