package MaidRepository.maid.service;

import MaidRepository.maid.dto.Msg91OtpResponseDTO;

public interface Msg91Service {
    Msg91OtpResponseDTO sendOtp(String mobile);
    Msg91OtpResponseDTO verifyOtp(String mobile, String otp);  // ← YEH ADD KARO
    Msg91OtpResponseDTO resendOtp(String mobile);
}