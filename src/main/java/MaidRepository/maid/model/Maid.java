package MaidRepository.maid.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maids")
public class Maid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dob;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String mobile;

    @Column(unique = true)
    @Email(message = "Invalid email address")
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", length = 50)
    private ServiceType serviceType;

    @Column(length = 1000)
    private String skills;

    private Integer experience;

    private String city;
    private String locality;
    private String pincode;

    private Double latitude;
    private Double longitude;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "document_urls", length = 2000)
    private String documentUrls;

    @Column(name = "document_types", length = 1000)
    private String documentTypes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MaidStatus status = MaidStatus.PENDING;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "hourly_rate")
    private Double hourlyRate;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum Gender { MALE, FEMALE, OTHER }
    public enum MaidStatus { ACTIVE, PENDING, INACTIVE }

    // Constructors
    public Maid() {}

    public Maid(String name, Gender gender, LocalDate dob, String mobile,
                String email, String password) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
    }

    // ALL GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public String getDocumentUrls() { return documentUrls; }
    public void setDocumentUrls(String documentUrls) { this.documentUrls = documentUrls; }

    public String getDocumentTypes() { return documentTypes; }
    public void setDocumentTypes(String documentTypes) { this.documentTypes = documentTypes; }

    public MaidStatus getStatus() { return status; }
    public void setStatus(MaidStatus status) { this.status = status; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = MaidStatus.PENDING;
        if (averageRating == null) averageRating = 0.0;
        if (isAvailable == null) isAvailable = true;
        if (isVerified == null) isVerified = false;

        // ✅ ADD THESE LINES FOR DEFAULT VALUES
        if (serviceType == null) serviceType = ServiceType.GENERAL;
        if (hourlyRate == null) hourlyRate = 200.0;

        // ✅ ADD THIS LINE FOR SKILLS
        if (skills == null) skills = "";

        // ✅ ADD THESE FOR OTHER COLUMNS THAT MIGHT BE NULL
        if (city == null) city = "";
        if (locality == null) locality = "";
        if (pincode == null) pincode = "";
        if (documentUrls == null) documentUrls = "";
        if (documentTypes == null) documentTypes = "";
        if (profilePhotoUrl == null) profilePhotoUrl = "";
        if (latitude == null) latitude = 0.0;
        if (longitude == null) longitude = 0.0;
        if (experience == null) experience = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}