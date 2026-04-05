package MaidRepository.maid.dto;

import MaidRepository.maid.model.ServiceType;
import lombok.Data;

@Data
public class NearbySearchDTO {
    private Long maidId;
    private String maidName;
    private ServiceType serviceType;
    private Double hourlyRate;
    private Double averageRating;
    private String profilePhotoUrl;
    private Double distanceKm;
    private Double durationMinutes;
    private Double latitude;
    private Double longitude;
    private Boolean isAvailable;
    private Integer experience;
    private String city;
}