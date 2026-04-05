package MaidRepository.maid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // Basic location fields (existing)
    private String city;

    // otp veryfication ====
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Column(name = "otp_verified")
    private Boolean otpVerified = false;

    @Column(name = "otp_verified_at")
    private LocalDateTime otpVerifiedAt;

    public enum AccountStatus {
        PENDING,    // Registered but OTP not verified
        ACTIVE,     // OTP verified
        BLOCKED,
        DELETED
    }



    // ========== NEW ADDRESS FIELDS ==========

    // Current active location for service (from address selection)
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

    // Address relationship - One user can have multiple addresses
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Address> addresses = new ArrayList<>();

    // ========== CONSTRUCTORS ==========

    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isVerified = false;
        this.hasCompletedProfile = false;
    }

    public User(String fullName, String mobile, String email, String password) {
        this.fullName = fullName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.isVerified = false;
        this.hasCompletedProfile = false;
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

    // ========== NEW GETTERS/SETTERS ==========

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


    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }

    public Boolean getOtpVerified() { return otpVerified; }
    public void setOtpVerified(Boolean otpVerified) { this.otpVerified = otpVerified; }

    public LocalDateTime getOtpVerifiedAt() { return otpVerifiedAt; }
    public void setOtpVerifiedAt(LocalDateTime otpVerifiedAt) { this.otpVerifiedAt = otpVerifiedAt; }
    // ========== UTILITY METHODS ==========

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", isVerified=" + isVerified +
                ", hasCompletedProfile=" + hasCompletedProfile +
                ", createdAt=" + createdAt +
                '}';
    }
}