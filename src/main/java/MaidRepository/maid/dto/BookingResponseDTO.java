package MaidRepository.maid.dto;

import MaidRepository.maid.model.Booking.BookingStatus;
import MaidRepository.maid.model.ServiceType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long maidId;
    private String maidName;
    private String maidProfilePhoto;      // ✅ NEW
    private ServiceType serviceType;       // ✅ NEW
    private String customerName;
    private String customerEmail;
    private String customerMobile;
    private String location;
    private Double latitude;
    private Double longitude;
    private String requirements;
    private Integer numberOfMaids;
    private LocalDate serviceDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationHours;
    private BookingStatus status;
    private Double totalAmount;
    private String maidNotes;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Helper flags for frontend
    private boolean canCancel;      // PENDING status ke liye true
    private boolean canChat;         // Always true if chat feature exists
    private boolean canRate;         // COMPLETED and not rated yet

    // Constructors
    public BookingResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getMaidId() { return maidId; }
    public void setMaidId(Long maidId) { this.maidId = maidId; }

    public String getMaidName() { return maidName; }
    public void setMaidName(String maidName) { this.maidName = maidName; }

    public String getMaidProfilePhoto() { return maidProfilePhoto; }
    public void setMaidProfilePhoto(String maidProfilePhoto) { this.maidProfilePhoto = maidProfilePhoto; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerMobile() { return customerMobile; }
    public void setCustomerMobile(String customerMobile) { this.customerMobile = customerMobile; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public Integer getNumberOfMaids() { return numberOfMaids; }
    public void setNumberOfMaids(Integer numberOfMaids) { this.numberOfMaids = numberOfMaids; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getMaidNotes() { return maidNotes; }
    public void setMaidNotes(String maidNotes) { this.maidNotes = maidNotes; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isCanCancel() { return canCancel; }
    public void setCanCancel(boolean canCancel) { this.canCancel = canCancel; }

    public boolean isCanChat() { return canChat; }
    public void setCanChat(boolean canChat) { this.canChat = canChat; }

    public boolean isCanRate() { return canRate; }
    public void setCanRate(boolean canRate) { this.canRate = canRate; }
}