package MaidRepository.maid.service;

import MaidRepository.maid.dto.Msg91OtpResponseDTO;

public interface OtpService {

    // ==================== SIMPLE METHODS ====================
    Msg91OtpResponseDTO sendOtp(String mobile);
    boolean verifyOTP(String mobile, String otp);
    void resendOTP(String mobile);
    void deleteOtp(String mobile);
    String generateAndSendOTP(String mobile, String email);  // ✅ 2 parameters only

    // ==================== BACKWARD COMPATIBILITY ====================
    default boolean verifyOTP(String mobile, String otp, String userType) {
        return verifyOTP(mobile, otp);
    }

    default boolean verifyOTP(String mobile, String otp, String userType, String purpose) {
        return verifyOTP(mobile, otp);
    }

    default void resendOTP(String mobile, String userType) {
        resendOTP(mobile);
    }

    default void deleteOtp(String mobile, String userType) {
        deleteOtp(mobile);
    }

    default Msg91OtpResponseDTO sendOtp(String mobile, String userType) {
        return sendOtp(mobile);
    }

    // ==================== COMMON METHODS ====================
    String generateOtp();
    boolean isTestMode();
}