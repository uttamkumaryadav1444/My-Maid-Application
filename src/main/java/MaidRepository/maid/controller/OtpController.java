package MaidRepository.maid.controller;

import MaidRepository.maid.dto.APIResponse;
import MaidRepository.maid.dto.Msg91OtpResponseDTO;
import MaidRepository.maid.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private static final Logger log = LoggerFactory.getLogger(OtpController.class);
    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    // ✅ SEND OTP - using sendOtp() method
    @PostMapping("/send")
    public ResponseEntity<APIResponse> sendOtp(@RequestParam String mobile,
                                               @RequestParam String userType) {
        try {
            log.info("Sending OTP to mobile: {}, userType: {}", mobile, userType);

            Msg91OtpResponseDTO response = otpService.sendOtp(mobile, userType);

            if (response != null && "success".equalsIgnoreCase(response.getType())) {
                return ResponseEntity.ok(APIResponse.success("OTP sent successfully", response));
            } else {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Failed to send OTP"));
            }

        } catch (Exception e) {
            log.error("Send OTP error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to send OTP: " + e.getMessage()));
        }
    }

    // ✅ VERIFY OTP - FIXED: using verifyOTP() not verifyOtp()
    @PostMapping("/verify")
    public ResponseEntity<APIResponse> verifyOtp(@RequestParam String mobile,
                                                 @RequestParam String otp,
                                                 @RequestParam String userType) {
        try {
            log.info("Verifying OTP for mobile: {}", mobile);

            // FIXED: verifyOTP (capital OTP) not verifyOtp
            boolean verified = otpService.verifyOTP(mobile, otp, userType);

            if (verified) {
                return ResponseEntity.ok(APIResponse.success("OTP verified successfully", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid OTP"));
            }

        } catch (Exception e) {
            log.error("Verify OTP error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Verification failed: " + e.getMessage()));
        }
    }

    // ✅ RESEND OTP - FIXED: using resendOTP() not resendOtp()
    @PostMapping("/resend")
    public ResponseEntity<APIResponse> resendOtp(@RequestParam String mobile,
                                                 @RequestParam String userType) {
        try {
            log.info("Resending OTP for mobile: {}", mobile);

            // FIXED: resendOTP (capital OTP) not resendOtp
            otpService.resendOTP(mobile, userType);

            return ResponseEntity.ok(APIResponse.success("OTP resent successfully", null));

        } catch (Exception e) {
            log.error("Resend OTP error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to resend OTP: " + e.getMessage()));
        }
    }
}