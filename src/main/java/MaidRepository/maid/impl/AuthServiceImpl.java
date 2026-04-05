package MaidRepository.maid.impl;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.User;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.model.User.AccountStatus;
import MaidRepository.maid.model.Maid.MaidStatus;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.service.AuthService;
import MaidRepository.maid.service.OtpService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaidRepository maidRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    // ==================== USER REGISTRATION ====================
    @Override
    public UserResponseDTO registerUser(UserRequestDTO request) {
        try {
            logger.info("Starting user registration for mobile: {}", request.getMobile());

            // Validate mobile
            if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
                throw new IllegalArgumentException("Invalid mobile number");
            }

            // Check if mobile already exists
            if (userRepository.existsByMobile(request.getMobile())) {
                throw new IllegalArgumentException("Mobile number already registered");
            }

            // Check if email already exists
            if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already registered");
            }

            // Validate password
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Password and confirm password do not match");
            }

            // Create user
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

            // ===== OTP with try-catch =====
            try {
                String otp = otpService.generateAndSendOTP(
                        savedUser.getMobile(),
                        savedUser.getEmail(),
                        "USER"
                );
                logger.info("OTP sent: {}", otp);
            } catch (Exception e) {
                logger.error("OTP failed but user saved: {}", e.getMessage());
            }
            // =============================

            UserResponseDTO response = convertUserToDTO(savedUser);
            response.setRequiresOtpVerification(true);
            response.setMessage("Registration successful. Please verify OTP.");

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

            // Validate mobile
            if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
                throw new IllegalArgumentException("Invalid mobile number");
            }

            // Check if mobile already exists
            if (maidRepository.existsByMobile(request.getMobile())) {
                throw new IllegalArgumentException("Mobile number already registered");
            }

            // Check if email already exists
            if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                    maidRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already registered");
            }

            // Validate password
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Password and confirm password do not match");
            }

            // Create maid
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

            // ===== OTP with try-catch =====
            try {
                String otp = otpService.generateAndSendOTP(
                        savedMaid.getMobile(),
                        savedMaid.getEmail(),
                        "MAID"
                );
                logger.info("OTP sent: {}", otp);
            } catch (Exception e) {
                logger.error("OTP failed but maid saved: {}", e.getMessage());
            }
            // =============================

            MaidResponseDTO response = convertMaidToDTO(savedMaid);
            response.setRequiresOtpVerification(true);
            response.setMessage("Registration successful. Please verify OTP.");

            return response;

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            logger.error("Maid registration failed: {}", e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
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
                maid.setVerifiedAt(LocalDateTime.now());

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
        return dto;
    }

    private MaidResponseDTO convertMaidToDTO(Maid maid) {
        MaidResponseDTO dto = new MaidResponseDTO();
        dto.setId(maid.getId());
        dto.setName(maid.getName());
        dto.setMobile(maid.getMobile());
        dto.setEmail(maid.getEmail());
        dto.setGender(maid.getGender());
        dto.setDob(maid.getDob());
        dto.setProfilePhotoUrl(maid.getProfilePhotoUrl());

        if (maid.getStatus() != null) {
            dto.setStatus(maid.getStatus());
        }

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

        return dto;
    }

    private String generateToken(String mobile, String userType) {
        return "token-" + mobile + "-" + System.currentTimeMillis();
    }

//    // AuthServiceImpl.java me ye method check karo:
//
//    private String generateToken(String mobile, String userType) {
//        // Simple token for demo (no JWT)
//        // return "token-" + mobile + "-" + System.currentTimeMillis();
//
//        // ✅ REAL JWT Token generation
//        return Jwts.builder()
//                .setSubject(mobile)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
//                .signWith(SignatureAlgorithm.HS256, "your-secret-key")
//                .compact();
//    }
}