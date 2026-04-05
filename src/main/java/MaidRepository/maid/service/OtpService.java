package MaidRepository.maid.service;

import MaidRepository.maid.dto.Msg91OtpResponseDTO;

public interface OtpService {

    // ==================== SEND OTP METHODS ====================

    // Send OTP via MSG91 - returns Msg91 response
    Msg91OtpResponseDTO sendOtp(String mobile, String userType);

    // Generate and send OTP - returns OTP string (for demo/backward compatibility)
    String generateAndSendOTP(String mobile, String email, String userType);

    // ==================== VERIFY OTP METHODS ====================

    // Verify OTP with 3 parameters
    boolean verifyOTP(String mobile, String otp, String userType);

    // Verify OTP with 4 parameters (with purpose)
    boolean verifyOTP(String mobile, String otp, String userType, String purpose);

    // ==================== OTHER OTP METHODS ====================

    // Resend OTP
    void resendOTP(String mobile, String userType);

    // Delete OTP
    void deleteOtp(String mobile, String userType);

    // Generate OTP string
    String generateOtp();
}