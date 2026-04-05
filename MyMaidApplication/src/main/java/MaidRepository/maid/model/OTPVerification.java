package MaidRepository.maid.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verifications")
public class OTPVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mobile;

    @Column(length = 10, nullable = true)  // ✅ Allow null
    private String otp;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @Column(name = "user_id")
    private Long userId;

    // ✅ Add this constructor for real OTP (no otp stored)
    public static OTPVerification createForRealOTP(String mobile, String requestId, LocalDateTime expiryTime) {
        OTPVerification otp = new OTPVerification();
        otp.setMobile(mobile);
        otp.setOtp(null);  // ✅ NULL for real OTP
        otp.setRequestId(requestId);
        otp.setExpiryTime(expiryTime);
        otp.setIsVerified(false);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setAttemptCount(0);
        return otp;
    }

    // ✅ Add this constructor for test mode OTP
    public static OTPVerification createForTestOTP(String mobile, String otp, String requestId, LocalDateTime expiryTime) {
        OTPVerification otpObj = new OTPVerification();
        otpObj.setMobile(mobile);
        otpObj.setOtp(otp);
        otpObj.setRequestId(requestId);
        otpObj.setExpiryTime(expiryTime);
        otpObj.setIsVerified(false);
        otpObj.setCreatedAt(LocalDateTime.now());
        otpObj.setAttemptCount(0);
        return otpObj;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public Integer getAttemptCount() { return attemptCount; }
    public void setAttemptCount(Integer attemptCount) { this.attemptCount = attemptCount; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (attemptCount == null) {
            attemptCount = 0;
        }
        if (isVerified == null) {
            isVerified = false;
        }
    }
}