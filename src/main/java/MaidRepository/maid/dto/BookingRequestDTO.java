package MaidRepository.maid.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingRequestDTO {
    private Long maidId;
    private String customerName;
    private String customerEmail;
    private String customerMobile;
    private String location;
    private Double latitude;
    private Double longitude;
    private String requirements;
    private Integer numberOfMaids = 1;
    private LocalDate serviceDate;
    private LocalTime startTime;
    private Integer durationHours = 1;

    public BookingRequestDTO() {}

    public Long getMaidId() { return maidId; }
    public void setMaidId(Long maidId) { this.maidId = maidId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerMobile() { return customerMobile; }
    public void setCustomerMobile(String customerMobile) { this.customerMobile = customerMobile; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public Integer getNumberOfMaids() { return numberOfMaids; }
    public void setNumberOfMaids(Integer numberOfMaids) { this.numberOfMaids = numberOfMaids; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }
}