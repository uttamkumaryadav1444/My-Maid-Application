package MaidRepository.maid.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verifications")
public class Otp {  // ← Changed from OtpEntity to Otp

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String otp;

    @Column(name = "user_type", nullable = false)
    private String userType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    // Constructors
    public Otp() {  // ← Changed constructor name
        this.createdAt = LocalDateTime.now();
        this.expiryTime = LocalDateTime.now().plusMinutes(10);
        this.attemptCount = 0;
    }

    public Otp(String mobile, String otp, String userType, Long userId) {  // ← Changed
        this.mobile = mobile;
        this.otp = otp;
        this.userType = userType;
        this.userId = userId;
        this.isVerified = false;
        this.createdAt = LocalDateTime.now();
        this.expiryTime = LocalDateTime.now().plusMinutes(10);
        this.attemptCount = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public Integer getAttemptCount() { return attemptCount; }
    public void setAttemptCount(Integer attemptCount) { this.attemptCount = attemptCount; }

    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public void incrementAttempt() {
        this.attemptCount++;
    }

    public boolean canResend() {
        // Can resend after 1 minute
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(1));
    }
}