package MaidRepository.maid.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class MaidSearchRequestDTO {

    private String serviceType;
    private LocalDate serviceDate;
    private LocalTime startTime;
    private Integer durationHours;
    private Integer page;   // add
    private Integer size;   // add
    private String city;


    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
