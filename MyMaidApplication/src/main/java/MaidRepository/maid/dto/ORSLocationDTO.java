package MaidRepository.maid.dto;

import lombok.Data;

@Data
public class ORSLocationDTO {
    private Double distanceKm;
    private Double durationMinutes;
    private Double sourceLat;
    private Double sourceLng;
    private Double destLat;
    private Double destLng;
    private String message;
}