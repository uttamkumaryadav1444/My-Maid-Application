package MaidRepository.maid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number (10 digits starting with 6-9)")
    private String mobile;

    @Column(unique = true)
    @Email(message = "Invalid email address")
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "has_completed_profile")
    private Boolean hasCompletedProfile = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Basic location fields
    private String city;

    // OTP verification fields
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Column(name = "otp_verified")
    private Boolean otpVerified = false;

    @Column(name = "otp_verified_at")
    private LocalDateTime otpVerifiedAt;

    // ✅ NEW: Subscription field for paid features
    @Column(name = "is_subscribed")
    private Boolean isSubscribed = false;

    @Column(name = "subscription_plan")
    private String subscriptionPlan = "FREE";

    @Column(name = "subscription_expiry")
    private LocalDateTime subscriptionExpiry;

    @Column(name = "subscription_activated")
    private Boolean subscriptionActivated = false;

    // ✅ NEW: Languages field for user
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_languages",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Set<Language> languages = new HashSet<>();

    // Current active location for service
    @Column(name = "current_city")
    private String currentCity;

    @Column(name = "current_area")
    private String currentArea;

    @Column(name = "current_locality")
    private String currentLocality;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "current_address_line")
    private String currentAddressLine;

    @Column(name = "current_pincode")
    private String currentPincode;

    // Address relationship
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Address> addresses = new ArrayList<>();

    public enum AccountStatus {
        PENDING, ACTIVE, BLOCKED, DELETED
    }


    // New fields for verification
    @Column(name = "selfie_photo_url")
    private String selfiePhotoUrl;

    @Column(name = "selfie_verified")
    private Boolean selfieVerified = false;

    @Column(name = "selfie_verified_at")
    private LocalDateTime selfieVerifiedAt;

    @Column(name = "aadhar_number")
    private String aadharNumber;

    @Column(name = "aadhar_photo_url")
    private String aadharPhotoUrl;

    @Column(name = "aadhar_verified")
    private Boolean aadharVerified = false;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "pan_photo_url")
    private String panPhotoUrl;

    @Column(name = "pan_verified")
    private Boolean panVerified = false;

    @Column(name = "verification_status")
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    public enum VerificationStatus {
        PENDING, SELFIE_SUBMITTED, DOCUMENTS_SUBMITTED, VERIFIED, REJECTED
    }


    // ========== CONSTRUCTORS ==========

    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isVerified = false;
        this.hasCompletedProfile = false;
        this.isSubscribed = false;
    }

    public User(String fullName, String mobile, String email, String password) {
        this.fullName = fullName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.isVerified = false;
        this.hasCompletedProfile = false;
        this.isSubscribed = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ========== LIFECYCLE METHODS ==========

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isVerified == null) isVerified = false;
        if (hasCompletedProfile == null) hasCompletedProfile = false;
        if (isSubscribed == null) isSubscribed = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========== HELPER METHODS ==========

    public void addAddress(Address address) {
        addresses.add(address);
        address.setUser(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setUser(null);
    }

    public boolean hasCurrentLocation() {
        return currentLatitude != null && currentLongitude != null;
    }

    // ========== GETTERS AND SETTERS ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Boolean getHasCompletedProfile() { return hasCompletedProfile; }
    public void setHasCompletedProfile(Boolean hasCompletedProfile) {
        this.hasCompletedProfile = hasCompletedProfile;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }

    public Boolean getOtpVerified() { return otpVerified; }
    public void setOtpVerified(Boolean otpVerified) { this.otpVerified = otpVerified; }

    public LocalDateTime getOtpVerifiedAt() { return otpVerifiedAt; }
    public void setOtpVerifiedAt(LocalDateTime otpVerifiedAt) { this.otpVerifiedAt = otpVerifiedAt; }

    // ✅ NEW: Subscription getter/setter
    public Boolean getIsSubscribed() { return isSubscribed; }
    public void setIsSubscribed(Boolean isSubscribed) { this.isSubscribed = isSubscribed; }

    public String getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(String subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    public LocalDateTime getSubscriptionExpiry() { return subscriptionExpiry; }
    public void setSubscriptionExpiry(LocalDateTime subscriptionExpiry) { this.subscriptionExpiry = subscriptionExpiry; }
    public Boolean getSubscriptionActivated() { return subscriptionActivated; }
    public void setSubscriptionActivated(Boolean subscriptionActivated) { this.subscriptionActivated = subscriptionActivated; }
    // ✅ NEW: Languages getter/setter
    public Set<Language> getLanguages() { return languages; }
    public void setLanguages(Set<Language> languages) { this.languages = languages; }

    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }

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



    public String getSelfiePhotoUrl() { return selfiePhotoUrl; }
    public void setSelfiePhotoUrl(String selfiePhotoUrl) { this.selfiePhotoUrl = selfiePhotoUrl; }

    public Boolean getSelfieVerified() { return selfieVerified; }
    public void setSelfieVerified(Boolean selfieVerified) { this.selfieVerified = selfieVerified; }

    public LocalDateTime getSelfieVerifiedAt() { return selfieVerifiedAt; }
    public void setSelfieVerifiedAt(LocalDateTime selfieVerifiedAt) { this.selfieVerifiedAt = selfieVerifiedAt; }

    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }

    public String getAadharPhotoUrl() { return aadharPhotoUrl; }
    public void setAadharPhotoUrl(String aadharPhotoUrl) { this.aadharPhotoUrl = aadharPhotoUrl; }

    public Boolean getAadharVerified() { return aadharVerified; }
    public void setAadharVerified(Boolean aadharVerified) { this.aadharVerified = aadharVerified; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getPanPhotoUrl() { return panPhotoUrl; }
    public void setPanPhotoUrl(String panPhotoUrl) { this.panPhotoUrl = panPhotoUrl; }

    public Boolean getPanVerified() { return panVerified; }
    public void setPanVerified(Boolean panVerified) { this.panVerified = panVerified; }

    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }



    public boolean hasActiveSubscription() {
        return subscriptionActivated &&
                subscriptionExpiry != null &&
                subscriptionExpiry.isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", isVerified=" + isVerified +
                ", isSubscribed=" + isSubscribed +
                ", hasCompletedProfile=" + hasCompletedProfile +
                ", createdAt=" + createdAt +
                '}';
    }
}