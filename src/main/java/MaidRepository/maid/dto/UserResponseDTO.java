package MaidRepository.maid.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String mobile;
    private String email;
    private String city;
    private String profilePhotoUrl;
    private Boolean isVerified;
    private String accountStatus;
    private boolean requiresOtpVerification;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> extraData;


    // Default constructor
    public UserResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public boolean isRequiresOtpVerification() { return requiresOtpVerification; }
    public void setRequiresOtpVerification(boolean requiresOtpVerification) {
        this.requiresOtpVerification = requiresOtpVerification;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Map<String, Object> getExtraData() { return extraData; }
    public void setExtraData(Map<String, Object> extraData) { this.extraData = extraData; }
}