package MaidRepository.maid.impl;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.*;
import MaidRepository.maid.model.User.AccountStatus;
import MaidRepository.maid.model.Maid.MaidStatus;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.repository.BlacklistedTokenRepository;
import MaidRepository.maid.service.AuthService;
import MaidRepository.maid.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;           // ✅ YEH IMPORT ADD KARO

// ... rest of the code

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final MaidRepository maidRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           MaidRepository maidRepository,
                           PasswordEncoder passwordEncoder,
                           OtpService otpService,
                           BlacklistedTokenRepository blacklistedTokenRepository) {
        this.userRepository = userRepository;
        this.maidRepository = maidRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    // ==================== USER REGISTRATION ====================
    @Override
    public UserResponseDTO registerUser(UserRequestDTO request) {
        try {
            logger.info("Starting user registration for mobile: {}", request.getMobile());

            if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
                throw new IllegalArgumentException("Invalid mobile number");
            }

            if (userRepository.existsByMobile(request.getMobile())) {
                throw new IllegalArgumentException("Mobile number already registered");
            }

            if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already registered");
            }

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Password and confirm password do not match");
            }

            User user = new User();
            user.setFullName(request.getFullName());
            user.setMobile(request.getMobile());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setAccountStatus(AccountStatus.PENDING);
            user.setOtpVerified(false);
            user.setIsVerified(false);

            User savedUser = userRepository.save(user);
            logger.info("User saved with ID: {}", savedUser.getId());

            try {
                String otp = otpService.generateAndSendOTP(
                        savedUser.getMobile(),
                        savedUser.getEmail());
                logger.info("OTP sent: {}", otp);
            } catch (Exception e) {
                logger.error("OTP failed but user saved: {}", e.getMessage());
            }

            UserResponseDTO response = convertUserToDTO(savedUser);
            // ❌ Remove these lines if methods don't exist
            // response.setRequiresOtpVerification(true);
            // response.setMessage("Registration successful. Please verify OTP.");

            return response;

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    // ==================== MAID REGISTRATION ====================
    @Override
    public MaidResponseDTO registerMaid(MaidRequestDTO request) {
        try {
            logger.info("Starting maid registration for mobile: {}", request.getMobile());

            if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
                throw new IllegalArgumentException("Invalid mobile number");
            }

            if (maidRepository.existsByMobile(request.getMobile())) {
                throw new IllegalArgumentException("Mobile number already registered");
            }

            if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                    maidRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already registered");
            }

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Password and confirm password do not match");
            }

            Maid maid = new Maid();
            maid.setName(request.getFullName());
            maid.setGender(request.getGender());
            maid.setDob(request.getDob());
            maid.setMobile(request.getMobile());
            maid.setEmail(request.getEmail());
            maid.setPassword(passwordEncoder.encode(request.getPassword()));
            maid.setStatus(MaidStatus.PENDING);
            maid.setIsVerified(false);
            maid.setIsAvailable(false);

            Maid savedMaid = maidRepository.save(maid);
            logger.info("Maid saved with ID: {}", savedMaid.getId());

            try {
                String otp = otpService.generateAndSendOTP(
                        savedMaid.getMobile(),
                        savedMaid.getEmail());
                logger.info("OTP sent: {}", otp);
            } catch (Exception e) {
                logger.error("OTP failed but maid saved: {}", e.getMessage());
            }

            MaidResponseDTO response = convertMaidToDTO(savedMaid);
            // ❌ Remove these lines if methods don't exist
            // response.setRequiresOtpVerification(true);
            // response.setMessage("Registration successful. Please verify OTP.");

            return response;

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            logger.error("Maid registration failed: {}", e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    // ==================== OTP VERIFY + AUTO ACTIVATE ====================
    @Override
    public boolean verifyOTP(String mobile, String otp) {
        try {
            logger.info("Verifying OTP for mobile: {} with auto-activate", mobile);

            // 1. Pehle OTP verify karo
            boolean isVerified = otpService.verifyOTP(mobile, otp);

            if (isVerified) {
                // 2. ✅ Check if mobile belongs to USER
                Optional<User> userOpt = userRepository.findByMobile(mobile);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    user.setAccountStatus(AccountStatus.ACTIVE);
                    user.setIsVerified(true);
                    user.setOtpVerified(true);
                    user.setOtpVerifiedAt(LocalDateTime.now());

                    userRepository.save(user);
                    logger.info("✅ User activated automatically: {}", mobile);
                    return true;
                }

                // 3. ✅ Check if mobile belongs to MAID
                Optional<Maid> maidOpt = maidRepository.findByMobile(mobile);
                if (maidOpt.isPresent()) {
                    Maid maid = maidOpt.get();
                    maid.setStatus(MaidStatus.ACTIVE);
                    maid.setIsVerified(true);
                    maid.setIsAvailable(true);

                    maidRepository.save(maid);
                    logger.info("✅ Maid activated automatically: {}", mobile);
                    return true;
                }

                // 4. Agar koi match nahi mila
                logger.warn("Mobile number not found in any table: {}", mobile);
                return false;

            } else {
                logger.warn("❌ Invalid OTP for mobile: {}", mobile);
                return false;
            }

        } catch (Exception e) {
            logger.error("OTP verification + activation failed: {}", e.getMessage());
            return false;
        }
    }
    // ==================== LOGIN ====================
    @Override
    public LoginResponseDTO login(String username, String password, String userType) {
        try {
            logger.info("Login attempt for {}: {}", userType, username);

            if ("USER".equalsIgnoreCase(userType)) {
                User user = userRepository.findByMobile(username)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                if (user.getAccountStatus() != AccountStatus.ACTIVE) {
                    throw new IllegalArgumentException("Account not activated. Please verify OTP first.");
                }

                if (!passwordEncoder.matches(password, user.getPassword())) {
                    throw new IllegalArgumentException("Invalid password");
                }

                LoginResponseDTO response = new LoginResponseDTO();
                response.setId(user.getId());
                response.setUserId(user.getId());
                response.setName(user.getFullName());
                response.setMobile(user.getMobile());
                response.setEmail(user.getEmail());
                response.setUserType("USER");
                response.setToken(generateToken(user.getMobile(), "USER"));
                response.setProfileComplete(user.getHasCompletedProfile() != null ? user.getHasCompletedProfile() : false);
                response.setRedirectTo(response.isProfileComplete() ? "dashboard" : "location");

                return response;

            } else if ("MAID".equalsIgnoreCase(userType)) {
                Maid maid = maidRepository.findByMobile(username)
                        .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

                if (maid.getStatus() != MaidStatus.ACTIVE) {
                    throw new IllegalArgumentException("Account not activated. Please verify OTP first.");
                }

                if (!passwordEncoder.matches(password, maid.getPassword())) {
                    throw new IllegalArgumentException("Invalid password");
                }

                LoginResponseDTO response = new LoginResponseDTO();
                response.setId(maid.getId());
                response.setUserId(maid.getId());
                response.setName(maid.getName());
                response.setMobile(maid.getMobile());
                response.setEmail(maid.getEmail());
                response.setUserType("MAID");
                response.setToken(generateToken(maid.getMobile(), "MAID"));

                boolean profileComplete = maid.getCity() != null && !maid.getCity().isEmpty() &&
                        maid.getHourlyRate() != null && maid.getHourlyRate() > 0;
                response.setProfileComplete(profileComplete);
                response.setRedirectTo(profileComplete ? "maid-dashboard" : "maid-profile");

                return response;
            } else {
                throw new IllegalArgumentException("Invalid user type");
            }

        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    // ==================== CHANGE PASSWORD ====================
    @Override
    public boolean changePassword(ChangePasswordDTO request, String userType) {
        try {
            if ("USER".equalsIgnoreCase(userType)) {
                User user = userRepository.findByMobile(request.getMobile())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                if (request.getOldPassword() != null && !request.getOldPassword().isEmpty()) {
                    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                        throw new IllegalArgumentException("Old password is incorrect");
                    }
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);

            } else if ("MAID".equalsIgnoreCase(userType)) {
                Maid maid = maidRepository.findByMobile(request.getMobile())
                        .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

                if (request.getOldPassword() != null && !request.getOldPassword().isEmpty()) {
                    if (!passwordEncoder.matches(request.getOldPassword(), maid.getPassword())) {
                        throw new IllegalArgumentException("Old password is incorrect");
                    }
                }

                maid.setPassword(passwordEncoder.encode(request.getNewPassword()));
                maidRepository.save(maid);
            } else {
                throw new IllegalArgumentException("Invalid user type");
            }

            logger.info("Password changed successfully for {}: {}", userType, request.getMobile());
            return true;

        } catch (Exception e) {
            logger.error("Password change failed: {}", e.getMessage());
            throw new RuntimeException("Password change failed: " + e.getMessage());
        }
    }

    // ==================== VERIFY AND ACTIVATE ====================
    @Override
    public OTPVerificationResponseDTO verifyAndActivate(String mobile, String otp, String userType) {
        try {
            logger.info("Verifying OTP for {}: {}", userType, mobile);

            boolean isVerified = otpService.verifyOTP(mobile, otp, userType, "REGISTRATION");

            if (!isVerified) {
                return new OTPVerificationResponseDTO(
                        false,
                        "Invalid or expired OTP",
                        mobile,
                        userType,
                        false,
                        null
                );
            }

            if ("USER".equalsIgnoreCase(userType)) {
                User user = userRepository.findByMobile(mobile)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                user.setAccountStatus(AccountStatus.ACTIVE);
                user.setOtpVerified(true);
                user.setIsVerified(true);
                user.setOtpVerifiedAt(LocalDateTime.now());

                userRepository.save(user);
                logger.info("User activated successfully: {}", mobile);

                return new OTPVerificationResponseDTO(
                        true,
                        "OTP verified successfully. Please complete your profile.",
                        mobile,
                        userType,
                        true,
                        "profile"
                );

            } else if ("MAID".equalsIgnoreCase(userType)) {
                Maid maid = maidRepository.findByMobile(mobile)
                        .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

                maid.setStatus(MaidStatus.ACTIVE);
                maid.setIsVerified(true);
                maid.setIsAvailable(true);
                // ❌ Remove if setVerifiedAt doesn't exist
                // maid.setVerifiedAt(LocalDateTime.now());

                maidRepository.save(maid);
                logger.info("Maid activated successfully: {}", mobile);

                return new OTPVerificationResponseDTO(
                        true,
                        "OTP verified successfully. Please complete your profile.",
                        mobile,
                        userType,
                        true,
                        "maid-profile"
                );
            } else {
                throw new IllegalArgumentException("Invalid user type");
            }

        } catch (Exception e) {
            logger.error("OTP verification failed: {}", e.getMessage());
            return new OTPVerificationResponseDTO(
                    false,
                    "Verification failed: " + e.getMessage(),
                    mobile,
                    userType,
                    false,
                    null
            );
        }
    }

    // ==================== TOKEN METHODS ====================

    private String generateToken(String mobile, String userType) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 6);
        return "token-" + mobile + "-" + timestamp + "-" + random;
    }

    public boolean isTokenValid(String token) {
        if (token == null || token.isEmpty()) return false;
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            token = token.trim();
            if (!token.startsWith("token-")) return false;
            if (blacklistedTokenRepository.existsByToken(token)) return false;
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractMobileFromToken(String token) {
        if (token == null || token.isEmpty()) return null;
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            token = token.trim();
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

    // ==================== HELPER METHODS ====================

    private UserResponseDTO convertUserToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setMobile(user.getMobile());
        dto.setEmail(user.getEmail());
        dto.setProfilePhotoUrl(user.getProfilePhotoUrl());
        dto.setIsVerified(user.getIsVerified());
        dto.setAccountStatus(user.getAccountStatus() != null ? user.getAccountStatus().name() : "PENDING");
        dto.setCity(user.getCity());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setIsSubscribed(user.getIsSubscribed());
        dto.setLanguages(user.getLanguages());
        return dto;
    }

    private MaidResponseDTO convertMaidToDTO(Maid maid) {
        MaidResponseDTO dto = new MaidResponseDTO();
        dto.setId(maid.getId());
        dto.setName(maid.getName());
        dto.setGender(maid.getGender());
        dto.setDob(maid.getDob());
        dto.setMobile(maid.getMobile());
        dto.setEmail(maid.getEmail());
        dto.setProfilePhotoUrl(maid.getProfilePhotoUrl());
        dto.setStatus(maid.getStatus());
        dto.setIsVerified(maid.getIsVerified());
        dto.setIsAvailable(maid.getIsAvailable());
        dto.setCreatedAt(maid.getCreatedAt());
        dto.setUpdatedAt(maid.getUpdatedAt());
        dto.setServiceType(maid.getServiceType());
        dto.setSkills(maid.getSkills());
        dto.setExperience(maid.getExperience());
        dto.setCity(maid.getCity());
        dto.setLocality(maid.getLocality());
        dto.setHourlyRate(maid.getHourlyRate());
        dto.setAverageRating(maid.getAverageRating());
        dto.setLanguages(maid.getLanguages());
        dto.setWorkType(maid.getWorkType());
        return dto;
    }
}