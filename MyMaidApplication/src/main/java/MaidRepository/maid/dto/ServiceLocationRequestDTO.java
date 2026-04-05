// ServiceLocationRequestDTO.java
package MaidRepository.maid.dto;

import jakarta.validation.constraints.*;

public class ServiceLocationRequestDTO {

    private Long addressId; // If using saved address

    private Boolean useGpsLocation = false;

    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private Double longitude;

    private String city;

    private String area;

    private String fullAddress;

    // Getters and Setters
    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public Boolean getUseGpsLocation() { return useGpsLocation; }
    public void setUseGpsLocation(Boolean useGpsLocation) { this.useGpsLocation = useGpsLocation; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
}