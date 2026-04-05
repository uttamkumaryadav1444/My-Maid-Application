package MaidRepository.maid.dto;

import MaidRepository.maid.model.Language;
import MaidRepository.maid.model.ServiceType;
import MaidRepository.maid.model.Maid.WorkType;
import lombok.Data;

@Data
public class AdvancedSearchRequestDTO {
    private String city;
    private ServiceType serviceType;
    private Double minHourlyRate;
    private Double maxHourlyRate;
    private Boolean isAvailable;
    private Integer minExperience;
    private Integer maxExperience;
    private Language language;
    private Boolean isVerified;
    private Double minRating;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    private WorkType workType;
    private int page = 0;
    private int size = 10;

    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public Double getMinHourlyRate() { return minHourlyRate; }
    public void setMinHourlyRate(Double minHourlyRate) { this.minHourlyRate = minHourlyRate; }

    public Double getMaxHourlyRate() { return maxHourlyRate; }
    public void setMaxHourlyRate(Double maxHourlyRate) { this.maxHourlyRate = maxHourlyRate; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Integer getMinExperience() { return minExperience; }
    public void setMinExperience(Integer minExperience) { this.minExperience = minExperience; }

    public Integer getMaxExperience() { return maxExperience; }
    public void setMaxExperience(Integer maxExperience) { this.maxExperience = maxExperience; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Double getMinRating() { return minRating; }
    public void setMinRating(Double minRating) { this.minRating = minRating; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Double radiusKm) { this.radiusKm = radiusKm; }

    public WorkType getWorkType() { return workType; }
    public void setWorkType(WorkType workType) { this.workType = workType; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}