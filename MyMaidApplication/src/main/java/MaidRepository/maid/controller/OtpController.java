package MaidRepository.maid.controller;

import MaidRepository.maid.dto.APIResponse;
import MaidRepository.maid.dto.Msg91OtpResponseDTO;
import MaidRepository.maid.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private static final Logger log = LoggerFactory.getLogger(OtpController.class);
    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<APIResponse> sendOtp(@RequestParam String mobile) {
        try {
            log.info("Sending OTP to mobile: {}", mobile);
            Msg91OtpResponseDTO response = otpService.sendOtp(mobile);
            return ResponseEntity.ok(APIResponse.success("OTP sent successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<APIResponse> verifyOtp(@RequestParam String mobile,
                                                 @RequestParam String otp) {
        try {
            log.info("Verifying OTP for mobile: {}", mobile);
            boolean verified = otpService.verifyOTP(mobile, otp);

            if (verified) {
                return ResponseEntity.ok(APIResponse.success("OTP verified successfully", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Verification failed: " + e.getMessage()));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<APIResponse> resendOtp(@RequestParam String mobile) {
        try {
            log.info("Resending OTP for mobile: {}", mobile);
            otpService.resendOTP(mobile);
            return ResponseEntity.ok(APIResponse.success("OTP resent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to resend OTP: " + e.getMessage()));
        }
    }
}