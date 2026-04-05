package MaidRepository.maid.dto;

import MaidRepository.maid.model.ServiceType;
import lombok.Data;

@Data
public class FreeSearchRequestDTO {
    private String city;
    private ServiceType serviceType;
    private Integer minExperience;
    private Double minRating;
    private int page = 0;
    private int size = 10;

    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public Integer getMinExperience() { return minExperience; }
    public void setMinExperience(Integer minExperience) { this.minExperience = minExperience; }

    public Double getMinRating() { return minRating; }
    public void setMinRating(Double minRating) { this.minRating = minRating; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}