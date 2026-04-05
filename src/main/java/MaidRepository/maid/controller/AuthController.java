package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.AuthService;
import MaidRepository.maid.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);  // ✅ FIXED

    private final AuthService authService;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final MaidRepository maidRepository;

    public AuthController(AuthService authService,
                          OtpService otpService,
                          UserRepository userRepository,
                          MaidRepository maidRepository) {
        this.authService = authService;
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.maidRepository = maidRepository;
    }

    // ✅ USER REGISTRATION
    @PostMapping("/user/register")
    public ResponseEntity<APIResponse> registerUser(@RequestBody UserRequestDTO request) {
        try {
            logger.info("Registering user with mobile: {}", request.getMobile());
            UserResponseDTO user = authService.registerUser(request);
            return ResponseEntity.ok(APIResponse.success("User registered successfully", user));
        } catch (Exception e) {
            logger.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    // ✅ MAID REGISTRATION
    @PostMapping("/maid/register")
    public ResponseEntity<APIResponse> registerMaid(@RequestBody MaidRequestDTO request) {
        try {
            logger.info("Registering maid with mobile: {}", request.getMobile());
            MaidResponseDTO maid = authService.registerMaid(request);
            return ResponseEntity.ok(APIResponse.success("Maid registered successfully", maid));
        } catch (Exception e) {
            logger.error("Maid registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    // ✅ LOGIN (USER & MAID)
    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody LoginRequestDTO request) {
        try {
            logger.info("Login attempt for username: {}", request.getUsername());
            LoginResponseDTO response = authService.login(
                    request.getUsername(),
                    request.getPassword(),
                    request.getUserType());
            return ResponseEntity.ok(APIResponse.success("Login successful", response));
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Login failed: " + e.getMessage()));
        }
    }

    // ✅ CHANGE PASSWORD
    @PostMapping("/change-password")
    public ResponseEntity<APIResponse> changePassword(@RequestBody ChangePasswordDTO request) {
        try {
            logger.info("Password change request for mobile: {}", request.getMobile());
            boolean changed = authService.changePassword(request, request.getUserType());
            if (changed) {
                return ResponseEntity.ok(APIResponse.success("Password changed successfully", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Failed to change password"));
            }
        } catch (Exception e) {
            logger.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to change password: " + e.getMessage()));
        }
    }

    // ✅ FORGOT PASSWORD - SEND OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<APIResponse> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        try {
            logger.info("Processing forgot password for mobile: {}", request.getMobile());

            // Validate input
            if (request.getMobile() == null || request.getMobile().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Mobile number is required"));
            }

            if (request.getUserType() == null || request.getUserType().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("User type is required"));
            }

            // Check if user exists based on userType
            if ("USER".equalsIgnoreCase(request.getUserType())) {
                userRepository.findByMobile(request.getMobile())
                        .orElseThrow(() -> new IllegalArgumentException("User not found with this mobile"));
            } else if ("MAID".equalsIgnoreCase(request.getUserType())) {
                maidRepository.findByMobile(request.getMobile())
                        .orElseThrow(() -> new IllegalArgumentException("Maid not found with this mobile"));
            } else {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid user type. Must be USER or MAID"));
            }

            // Generate and send OTP
            String otp = otpService.generateAndSendOTP(request.getMobile(), null, request.getUserType());

            return ResponseEntity.ok(APIResponse.success("OTP sent successfully to " + request.getMobile(), otp));

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Internal error in forgot-password: ", e);
            return ResponseEntity.status(500)
                    .body(APIResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    // ✅ RESET PASSWORD WITH OTP
    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        try {
            logger.info("Password reset request for mobile: {}", request.getMobile());

            // Verify OTP first
            boolean isOtpVerified = otpService.verifyOTP(
                    request.getMobile(),
                    request.getOtp(),
                    request.getUserType());

            if (!isOtpVerified) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid or expired OTP"));
            }

            // Create change password request
            ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
            changePasswordDTO.setMobile(request.getMobile());
            changePasswordDTO.setNewPassword(request.getNewPassword());
            changePasswordDTO.setUserType(request.getUserType());

            // Change password without old password (since it's reset)
            boolean changed = authService.changePassword(changePasswordDTO, request.getUserType());

            if (changed) {
                // Delete OTP after successful reset
                otpService.deleteOtp(request.getMobile(), request.getUserType());
                return ResponseEntity.ok(APIResponse.success("Password reset successfully", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Failed to reset password"));
            }
        } catch (Exception e) {
            logger.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to reset password: " + e.getMessage()));
        }
    }

    // ✅ SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<APIResponse> sendOtp(@RequestBody SendOtpRequestDTO request) {
        try {
            logger.info("Sending OTP to mobile: {}", request.getMobile());
            String otp = otpService.generateAndSendOTP(
                    request.getMobile(),
                    request.getEmail(),
                    request.getUserType());
            return ResponseEntity.ok(APIResponse.success("OTP sent successfully", otp));
        } catch (Exception e) {
            logger.error("Failed to send OTP: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to send OTP: " + e.getMessage()));
        }
    }

    // ✅ VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<APIResponse> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        try {
            logger.info("Verifying OTP for mobile: {}", request.getMobile());
            boolean isVerified = otpService.verifyOTP(
                    request.getMobile(),
                    request.getOtp(),
                    request.getUserType());

            if (isVerified) {
                return ResponseEntity.ok(APIResponse.success("OTP verified successfully", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid OTP"));
            }
        } catch (Exception e) {
            logger.error("OTP verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("OTP verification failed: " + e.getMessage()));
        }
    }

    // ✅ RESEND OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<APIResponse> resendOtp(@RequestBody ResendOtpRequestDTO request) {
        try {
            logger.info("Resending OTP to mobile: {}", request.getMobile());
            otpService.resendOTP(request.getMobile(), request.getUserType());
            return ResponseEntity.ok(APIResponse.success("OTP resent successfully", null));
        } catch (Exception e) {
            logger.error("Failed to resend OTP: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to resend OTP: " + e.getMessage()));
        }
    }
}