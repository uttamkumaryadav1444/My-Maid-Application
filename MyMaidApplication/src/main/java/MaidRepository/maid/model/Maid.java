package MaidRepository.maid.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "maids")
public class Maid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dob;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;

    @Column(length = 1000)
    private String skills;

    private Integer experience;

    private String city;
    private String locality;
    private String pincode;

    @Column(name = "hourly_rate")
    private Double hourlyRate;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "document_urls", length = 2000)
    private String documentUrls;

    @Column(name = "document_types", length = 1000)
    private String documentTypes;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private MaidStatus status = MaidStatus.PENDING;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    // ✅ NEW: Languages field
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "maid_languages",
            joinColumns = @JoinColumn(name = "maid_id"))
    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Set<Language> languages = new HashSet<>();

    // Work type (FULL_TIME / PART_TIME)
    @Enumerated(EnumType.STRING)
    @Column(name = "work_type")
    private WorkType workType = WorkType.FULL_TIME;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum Gender { MALE, FEMALE, OTHER }
    public enum MaidStatus { ACTIVE, PENDING, INACTIVE }
    public enum WorkType { FULL_TIME, PART_TIME }


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

    @Column(name = "last_location_latitude")
    private Double lastLocationLatitude;

    @Column(name = "last_location_longitude")
    private Double lastLocationLongitude;

    @Column(name = "last_location_updated_at")
    private LocalDateTime lastLocationUpdatedAt;

    public enum VerificationStatus {
        PENDING, SELFIE_SUBMITTED, DOCUMENTS_SUBMITTED, VERIFIED, REJECTED
    }


    // Constructors
    public Maid() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters (generate all)
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
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public String getDocumentUrls() { return documentUrls; }
    public void setDocumentUrls(String documentUrls) { this.documentUrls = documentUrls; }

    public String getDocumentTypes() { return documentTypes; }
    public void setDocumentTypes(String documentTypes) { this.documentTypes = documentTypes; }
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    public MaidStatus getStatus() { return status; }
    public void setStatus(MaidStatus status) { this.status = status; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }
    public Set<Language> getLanguages() { return languages; }
    public void setLanguages(Set<Language> languages) { this.languages = languages; }
    public WorkType getWorkType() { return workType; }
    public void setWorkType(WorkType workType) { this.workType = workType; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }


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

    public Double getLastLocationLatitude() { return lastLocationLatitude; }
    public void setLastLocationLatitude(Double lastLocationLatitude) {
        this.lastLocationLatitude = lastLocationLatitude;
    }

    public Double getLastLocationLongitude() { return lastLocationLongitude; }
    public void setLastLocationLongitude(Double lastLocationLongitude) {
        this.lastLocationLongitude = lastLocationLongitude;
    }

    public LocalDateTime getLastLocationUpdatedAt() { return lastLocationUpdatedAt; }
    public void setLastLocationUpdatedAt(LocalDateTime lastLocationUpdatedAt) {
        this.lastLocationUpdatedAt = lastLocationUpdatedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}