package MaidRepository.maid.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "maid_id", nullable = false)
    private Maid maid;

    @Column(nullable = false)
    private String customerName;

    private String customerEmail;

    @Column(nullable = false)
    private String customerMobile;

    @Column(nullable = false)
    private String location;

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(nullable = false)
    private Integer numberOfMaids = 1;

    @Column(nullable = false)
    private LocalDate serviceDate;

    @Column(nullable = false)
    private LocalTime startTime;

    private LocalTime endTime;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private RatingReview ratingReview;

    private Integer durationHours = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    private Double totalAmount;

    @Column(columnDefinition = "TEXT")
    private String maidNotes;

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum BookingStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELLED,
        COMPLETED
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Maid getMaid() { return maid; }
    public void setMaid(Maid maid) { this.maid = maid; }

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

    public RatingReview getRatingReview() { return ratingReview; }
    public void setRatingReview(RatingReview ratingReview) { this.ratingReview = ratingReview; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Calculate total amount if maid and duration are set
        if (maid != null && maid.getHourlyRate() != null && durationHours != null) {
            totalAmount = maid.getHourlyRate() * durationHours * numberOfMaids;
        }

        // Set end time if start time and duration are set
        if (startTime != null && durationHours != null) {
            endTime = startTime.plusHours(durationHours);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Recalculate total amount if any relevant field changes
        if (maid != null && maid.getHourlyRate() != null && durationHours != null) {
            totalAmount = maid.getHourlyRate() * durationHours * numberOfMaids;
        }
    }
}