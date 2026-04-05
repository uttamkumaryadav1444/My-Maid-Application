package MaidRepository.maid.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class MaidSearchCriteriaDTO {
    private String serviceType;          // required (e.g., "HOUSEKEEPING")
    private LocalDate date;               // specific date or start date for range
    private LocalTime time;                // start time
    private Integer durationHours;         // duration in hours
    private String city;                   // optional (defaults to user's city)
    private String locality;
    private Double latitude;               // user's current location for nearby
    private Double longitude;
    private Double radiusKm;                // for nearby search (default 10 km)
    private String filter;                  // "TOP_RATED", "NEARBY", "BUDGET"
    private String availability;            // "NOW", "TODAY", "TOMORROW", "WEEK", "MONTH"
    private int offset = 0;
    private int limit = 20;
    private String language;

    // getters and setters (generated automatically, but shown here for clarity)
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Double radiusKm) { this.radiusKm = radiusKm; }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}