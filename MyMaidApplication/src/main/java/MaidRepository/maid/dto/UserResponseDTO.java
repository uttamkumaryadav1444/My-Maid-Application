package MaidRepository.maid.dto;

import MaidRepository.maid.model.Language;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

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

    // ✅ NEW: Subscription field
    private Boolean isSubscribed;

    // ✅ NEW: Languages field
    private Set<Language> languages;

    // Current location fields
    private String currentCity;
    private String currentArea;
    private String currentLocality;
    private Double currentLatitude;
    private Double currentLongitude;
    private String currentAddressLine;
    private String currentPincode;

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

    // ✅ NEW Getters/Setters
    public Boolean getIsSubscribed() { return isSubscribed; }
    public void setIsSubscribed(Boolean isSubscribed) { this.isSubscribed = isSubscribed; }

    public Set<Language> getLanguages() { return languages; }
    public void setLanguages(Set<Language> languages) { this.languages = languages; }

    public String getCurrentCity() { return currentCity; }
    public void setCurrentCity(String currentCity) { this.currentCity = currentCity; }

    public String getCurrentArea() { return currentArea; }
    public void setCurrentArea(String currentArea) { this.currentArea = currentArea; }

    public String getCurrentLocality() { return currentLocality; }
    public void setCurrentLocality(String currentLocality) { this.currentLocality = currentLocality; }

    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }

    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }

    public String getCurrentAddressLine() { return currentAddressLine; }
    public void setCurrentAddressLine(String currentAddressLine) { this.currentAddressLine = currentAddressLine; }

    public String getCurrentPincode() { return currentPincode; }
    public void setCurrentPincode(String currentPincode) { this.currentPincode = currentPincode; }
}