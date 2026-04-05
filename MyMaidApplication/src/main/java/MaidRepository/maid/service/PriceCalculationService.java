package MaidRepository.maid.service;

import MaidRepository.maid.model.Maid;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculationService {

    // Constants for calculations
    private static final int HOURS_PER_DAY = 8;
    private static final int DAYS_PER_WEEK = 5;
    private static final int DAYS_PER_MONTH = 22;

    /**
     * Calculate total amount based on booking type
     */
    public double calculateAmount(Maid maid, String bookingType,
                                  Integer hours, Integer days,
                                  Integer weeks, Integer months) {

        double hourlyRate = maid.getHourlyRate();

        switch(bookingType.toUpperCase()) {
            case "HOURLY":
                return hourlyRate * hours;

            case "DAILY":
                return hourlyRate * HOURS_PER_DAY * days;

            case "WEEKLY":
                return hourlyRate * HOURS_PER_DAY * DAYS_PER_WEEK * weeks;

            case "MONTHLY":
                return hourlyRate * HOURS_PER_DAY * DAYS_PER_MONTH * months;

            default:
                throw new IllegalArgumentException("Invalid booking type: " + bookingType);
        }
    }

    /**
     * Calculate total hours based on booking type
     */
    public int calculateTotalHours(String bookingType,
                                   Integer hours, Integer days,
                                   Integer weeks, Integer months) {

        switch(bookingType.toUpperCase()) {
            case "HOURLY":
                return hours;

            case "DAILY":
                return HOURS_PER_DAY * days;

            case "WEEKLY":
                return HOURS_PER_DAY * DAYS_PER_WEEK * weeks;

            case "MONTHLY":
                return HOURS_PER_DAY * DAYS_PER_MONTH * months;

            default:
                return 0;
        }
    }

    /**
     * Get display description
     */
    public String getBookingDescription(String bookingType,
                                        Integer hours, Integer days,
                                        Integer weeks, Integer months) {

        switch(bookingType.toUpperCase()) {
            case "HOURLY":
                return hours + " hour(s)";

            case "DAILY":
                return days + " day(s)";

            case "WEEKLY":
                return weeks + " week(s)";

            case "MONTHLY":
                return months + " month(s)";

            default:
                return "Booking";
        }
    }
}