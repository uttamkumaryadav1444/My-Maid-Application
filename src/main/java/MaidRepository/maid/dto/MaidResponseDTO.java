package MaidRepository.maid.dto;

import MaidRepository.maid.model.Maid.Gender;
import MaidRepository.maid.model.Maid.MaidStatus;
import MaidRepository.maid.model.ServiceType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MaidResponseDTO {
    private Long id;
    private String name;
    private Gender gender;
    private LocalDate dob;
    private String mobile;
    private String email;
    private ServiceType serviceType;
    private String skills;
    private Integer experience;
    private String city;
    private String locality;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private String profilePhotoUrl;
    private MaidStatus status;
    private Boolean isVerified;
    private Boolean isAvailable;
    private Double averageRating;
    private Double hourlyRate;
    private boolean requiresOtpVerification;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public MaidResponseDTO() {}

    // Getters and Setters
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

    public MaidStatus getStatus() { return status; }
    public void setStatus(MaidStatus status) { this.status = status; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

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
}