package MaidRepository.maid.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceType {
    DAILY_COOK("Daily Cook"),
    CHEF("Chef"),
    PET_CARE("Pet Care"),
    BABY_CARE("Baby Care"),
    SENIOR_CARE("Senior Care"),
    LAUNDRY_CARE("Laundry Care"),
    KITCHEN_HELP("Kitchen Help"),
    HOME_CLEANING("Home Cleaning");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static ServiceType fromValue(String value) {
        for (ServiceType type : ServiceType.values()) {
            if (type.displayName.equalsIgnoreCase(value) ||
                    type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid service type: " + value);
    }

    public static ServiceType fromCode(String code) {
        for (ServiceType type : ServiceType.values()) {
            if (type.name().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid service type code: " + code);
    }
}