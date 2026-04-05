package MaidRepository.maid.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class EmailService {

    public boolean sendOtpEmail(String email, String otp) {
        // Dummy implementation
        System.out.println("Sending OTP " + otp + " to email: " + email);
        return true;
    }

    public boolean sendBookingConfirmation(String userEmail, String userName,
                                           String maidName, LocalDate serviceDate,
                                           LocalTime serviceTime) {
        // Dummy implementation
        String message = String.format(
                "Booking Confirmed!\n\n" +
                        "Dear %s,\n" +
                        "Your booking with %s has been confirmed.\n" +
                        "Date: %s\n" +
                        "Time: %s\n\n" +
                        "Thank you for using MaidRepository!",
                userName, maidName, serviceDate, serviceTime
        );

        System.out.println("Sending booking confirmation to: " + userEmail);
        System.out.println("Message: " + message);
        return true;
    }

    public boolean sendBookingNotificationToMaid(String maidEmail, String maidName,
                                                 String userName, LocalDate serviceDate,
                                                 LocalTime serviceTime) {
        // Dummy implementation
        String message = String.format(
                "New Booking Request!\n\n" +
                        "Dear %s,\n" +
                        "You have a new booking request from %s.\n" +
                        "Date: %s\n" +
                        "Time: %s\n\n" +
                        "Please check your dashboard to accept or reject.",
                maidName, userName, serviceDate, serviceTime
        );

        System.out.println("Sending booking notification to maid: " + maidEmail);
        System.out.println("Message: " + message);
        return true;
    }
}