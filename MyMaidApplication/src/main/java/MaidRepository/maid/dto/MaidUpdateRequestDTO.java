package MaidRepository.maid.dto;

import MaidRepository.maid.model.Language;
import MaidRepository.maid.model.ServiceType;
import MaidRepository.maid.model.Maid.Gender;
import MaidRepository.maid.model.Maid.MaidStatus;
import java.time.LocalDate;
import java.util.Set;

public class MaidUpdateRequestDTO {
    private String name;
    private Gender gender;
    private LocalDate dob;
    private String email;
    private String city;
    private String locality;
    private String pincode;
    private ServiceType serviceType;
    private String skills;
    private Integer experience;
    private Double hourlyRate;
    private MaidStatus status;
    private Boolean isAvailable;
    private Set<Language> languages;  // ✅ ADD THIS

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public MaidStatus getStatus() { return status; }
    public void setStatus(MaidStatus status) { this.status = status; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    // ✅ ADD THESE
    public Set<Language> getLanguages() { return languages; }
    public void setLanguages(Set<Language> languages) { this.languages = languages; }
}