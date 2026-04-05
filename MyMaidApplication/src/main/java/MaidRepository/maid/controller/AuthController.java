package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.repository.BlacklistedTokenRepository;
import MaidRepository.maid.model.BlacklistedToken;
import MaidRepository.maid.service.AuthService;
import MaidRepository.maid.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final MaidRepository maidRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public AuthController(AuthService authService,
                          OtpService otpService,
                          UserRepository userRepository,
                          MaidRepository maidRepository,
                          BlacklistedTokenRepository blacklistedTokenRepository) {
        this.authService = authService;
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.maidRepository = maidRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
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

            // Validate mobile
            if (request.getMobile() == null || request.getMobile().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Mobile number is required"));
            }

            if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid mobile number"));
            }

            // Validate user type
            if (request.getUserType() == null || request.getUserType().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("User type is required"));
            }

            // Check if user exists
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

            // ✅ Generate and send OTP - 2 parameters only
            String otpRequestId = otpService.generateAndSendOTP(request.getMobile(), null);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent successfully");
            response.put("mobile", request.getMobile());
            response.put("requestId", otpRequestId);

            return ResponseEntity.ok(APIResponse.success("OTP sent successfully", response));

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

            // Validate mobile
            if (request.getMobile() == null || !request.getMobile().matches("^[6-9]\\d{9}$")) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid mobile number"));
            }

            // Validate OTP
            if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("OTP is required"));
            }

            // Validate passwords
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("New password is required"));
            }

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Passwords do not match"));
            }

            // ✅ Verify OTP - 2 parameters
            boolean isOtpVerified = otpService.verifyOTP(request.getMobile(), request.getOtp());

            if (!isOtpVerified) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid or expired OTP"));
            }

            // Create change password request
            ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
            changePasswordDTO.setMobile(request.getMobile());
            changePasswordDTO.setNewPassword(request.getNewPassword());
            changePasswordDTO.setUserType(request.getUserType());

            // Change password
            boolean changed = authService.changePassword(changePasswordDTO, request.getUserType());

            if (changed) {
                // Delete OTP after successful reset - 1 parameter only
                otpService.deleteOtp(request.getMobile());

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Password reset successfully");
                response.put("mobile", request.getMobile());

                return ResponseEntity.ok(APIResponse.success("Password reset successfully", response));
            } else {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Failed to reset password"));
            }
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to reset password: " + e.getMessage()));
        }
    }

    // ==================== SIMPLE OTP APIS ====================

    // ✅ SEND OTP - Simple (Sirf mobile number)
    // ✅ SEND OTP - Simple (Sirf mobile number)
    @PostMapping("/send-otp")
    public ResponseEntity<APIResponse> sendOtp(@RequestBody SendOtpRequestDTO request) {
        try {
            logger.info("Sending OTP to mobile: {}", request.getMobile());

            // Validate mobile
            if (request.getMobile() == null || !request.getMobile().matches("^[6-9]\\d{9}$")) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid mobile number"));
            }

            // ✅ Generate and send OTP - 2 parameters only
            Msg91OtpResponseDTO otpResponse = otpService.sendOtp(request.getMobile());

            // 🔥 SendOtpRequestDTO में email नहीं है, तो null भेजें
            // String otpRequestId = otpService.generateAndSendOTP(request.getMobile(), request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent successfully");
            response.put("mobile", request.getMobile());
            response.put("requestId", otpResponse.getRequestId());

            // ✅ OTP को JSON में भी दिखाएं (सिर्फ test mode में)
            if (otpService.isTestMode() && otpResponse.getOtp() != null) {
                response.put("otp", otpResponse.getOtp());
                logger.info("🔧 Test Mode OTP: {}", otpResponse.getOtp());
            }

            return ResponseEntity.ok(APIResponse.success("OTP sent successfully", response));

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to send OTP: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to send OTP: " + e.getMessage()));
        }
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<APIResponse> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        try {
            logger.info("Verifying OTP for mobile: {}", request.getMobile());

            // Validate
            if (request.getMobile() == null || !request.getMobile().matches("^[6-9]\\d{9}$")) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid mobile number"));
            }
            if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("OTP is required"));
            }

            // ✅ Single step: Verify OTP and Auto-Activate (NO USERTYPE NEEDED)
            boolean isVerified = authService.verifyOTP(
                    request.getMobile(),
                    request.getOtp()
            );

            if (isVerified) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "OTP verified successfully. Account activated!");
                response.put("mobile", request.getMobile());
                response.put("otp", request.getOtp());

                return ResponseEntity.ok(APIResponse.success("OTP verified and account activated", response));
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

    // ✅ RESEND OTP - Simple (Sirf mobile number)
    @PostMapping("/resend-otp")
    public ResponseEntity<APIResponse> resendOtp(@RequestBody ResendOtpRequestDTO request) {
        try {
            logger.info("Resending OTP to mobile: {}", request.getMobile());

            // Validate mobile
            if (request.getMobile() == null || !request.getMobile().matches("^[6-9]\\d{9}$")) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid mobile number"));
            }

            // ✅ Resend OTP - 1 parameter only
            otpService.resendOTP(request.getMobile());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP resent successfully");
            response.put("mobile", request.getMobile());

            return ResponseEntity.ok(APIResponse.success("OTP resent successfully", response));

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to resend OTP: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to resend OTP: " + e.getMessage()));
        }
    }

    // ✅ LOGOUT
    // In logout method - make sure BlacklistedToken has proper setters
    @PostMapping("/logout")
    public ResponseEntity<APIResponse> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("Logout request received");

            String token = extractTokenFromHeader(authHeader);

            if (token == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid token"));
            }

            if (blacklistedTokenRepository.existsByToken(token)) {
                return ResponseEntity.ok(APIResponse.success("Already logged out", null));
            }

            BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setToken(token);
            blacklistedToken.setExpiryTime(LocalDateTime.now().plusDays(7));

            blacklistedTokenRepository.save(blacklistedToken);

            logger.info("Token blacklisted successfully");

            Map<String, String> data = new HashMap<>();
            data.put("message", "Logged out successfully");

            return ResponseEntity.ok(APIResponse.success("Logout successful", data));

        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Logout failed: " + e.getMessage()));
        }
    }

    // ==================== HELPER METHODS ====================

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return null;
        }
        String header = authHeader.trim();
        if (header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return header;
    }

    private String extractMobileFromToken(String token) {
        if (token == null || token.isEmpty()) return null;
        try {
            if (token.startsWith("token-")) {
                String[] parts = token.split("-");
                if (parts.length >= 2) {
                    return parts[1];
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting mobile from token: {}", e.getMessage());
        }
        return null;
    }

    public String extractMobileFromAuthHeader(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        return extractMobileFromToken(token);
    }

    public String getUserTypeFromMobile(String mobile) {
        if (mobile == null) return null;
        if (userRepository.findByMobile(mobile).isPresent()) {
            return "USER";
        } else if (maidRepository.findByMobile(mobile).isPresent()) {
            return "MAID";
        }
        return null;
    }

    public String getUserTypeFromToken(String token) {
        String mobile = extractMobileFromToken(token);
        return getUserTypeFromMobile(mobile);
    }
}