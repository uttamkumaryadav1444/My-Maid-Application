package MaidRepository.maid.impl;

import MaidRepository.maid.dto.Msg91OtpResponseDTO;
import MaidRepository.maid.model.OTPVerification;
import MaidRepository.maid.model.User;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.model.User.AccountStatus;
import MaidRepository.maid.model.Maid.MaidStatus;
import MaidRepository.maid.repository.OtpRepository;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.service.Msg91Service;
import MaidRepository.maid.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);

    private final Msg91Service msg91Service;
    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final MaidRepository maidRepository;

    @Value("${otp.expiry.minutes:10}")
    private int otpExpiryMinutes;

    public OtpServiceImpl(Msg91Service msg91Service,
                          OtpRepository otpRepository,
                          UserRepository userRepository,
                          MaidRepository maidRepository) {
        this.msg91Service = msg91Service;
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.maidRepository = maidRepository;
    }

    @Override
    public Msg91OtpResponseDTO sendOtp(String mobile, String userType) {
        try {
            log.info("Sending OTP via MSG91 to mobile: {}", mobile);

            // Delete old OTPs
            otpRepository.deleteByMobile(mobile);

            // ✅ Real MSG91 call - No demo mode
            Msg91OtpResponseDTO response = msg91Service.sendOtp(mobile);

            log.info("MSG91 Response: {}", response);

            // Optional: Save request_id for tracking
            OTPVerification otpEntity = new OTPVerification();
            otpEntity.setMobile(mobile);
            otpEntity.setOtp("MSG91-OTP");
            otpEntity.setRequestId(response.getRequestId());
            otpEntity.setUserType(userType);
            otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
            otpEntity.setIsVerified(false);
            otpRepository.save(otpEntity);

            return response;

        } catch (Exception e) {
            log.error("Failed to send OTP: {}", e.getMessage());
            throw new RuntimeException("OTP send failed: " + e.getMessage());
        }
    }

    @Override
    public String generateAndSendOTP(String mobile, String email, String userType) {
        // This method is for backward compatibility
        Msg91OtpResponseDTO response = sendOtp(mobile, userType);
        return response != null ? response.getRequestId() : null;
    }

    @Override
    public boolean verifyOTP(String mobile, String otp, String userType) {
        return verifyOTP(mobile, otp, userType, null);
    }

    @Override
    public boolean verifyOTP(String mobile, String otp, String userType, String purpose) {
        try {
            log.info("Verifying OTP for mobile: {}, otp: {}", mobile, otp);

            // Direct MSG91 verification
            Msg91OtpResponseDTO response = msg91Service.verifyOtp(mobile, otp);

            if (response != null && "success".equalsIgnoreCase(response.getType())) {
                log.info("✅ MSG91 verification successful for {}", mobile);

                // ✅ FIXED: List se update karo
                List<OTPVerification> otpList = otpRepository.findByMobile(mobile);
                for (OTPVerification otpEntity : otpList) {
                    otpEntity.setIsVerified(true);
                    otpEntity.setVerifiedAt(LocalDateTime.now());
                    otpRepository.save(otpEntity);
                }

                // Update user/maid status
                updateAccountStatus(mobile, userType, true);
                return true;
            }

            log.warn("❌ MSG91 verification failed for {}", mobile);
            return false;

        } catch (Exception e) {
            log.error("OTP verification failed: {}", e.getMessage());
            return false;
        }
    }

    private void updateAccountStatus(String mobile, String userType, boolean verified) {
        try {
            if (verified) {
                if ("USER".equalsIgnoreCase(userType)) {
                    User user = userRepository.findByMobile(mobile).orElse(null);
                    if (user != null) {
                        user.setAccountStatus(AccountStatus.ACTIVE);
                        user.setOtpVerified(true);
                        user.setIsVerified(true);
                        userRepository.save(user);
                        log.info("User account activated: {}", mobile);
                    }
                } else if ("MAID".equalsIgnoreCase(userType)) {
                    Maid maid = maidRepository.findByMobile(mobile).orElse(null);
                    if (maid != null) {
                        maid.setStatus(MaidStatus.ACTIVE);
                        maid.setIsVerified(true);
                        maidRepository.save(maid);
                        log.info("Maid account activated: {}", mobile);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to update account status: {}", e.getMessage());
        }
    }

    @Override
    public void resendOTP(String mobile, String userType) {
        try {
            log.info("Resending OTP for mobile: {}", mobile);

            // Delete old OTPs
            otpRepository.deleteByMobile(mobile);

            // Resend OTP via MSG91
            Msg91OtpResponseDTO response = msg91Service.resendOtp(mobile);

            if (response != null && "success".equalsIgnoreCase(response.getType())) {
                log.info("OTP resent successfully for {}", mobile);
            } else {
                throw new RuntimeException("Failed to resend OTP");
            }

        } catch (Exception e) {
            log.error("Failed to resend OTP: {}", e.getMessage());
            throw new RuntimeException("Failed to resend OTP");
        }
    }

    @Override
    public void deleteOtp(String mobile, String userType) {
        try {
            log.info("Deleting OTPs for mobile: {}", mobile);
            otpRepository.deleteByMobile(mobile);
        } catch (Exception e) {
            log.error("Failed to delete OTP: {}", e.getMessage());
        }
    }

    @Override
    public String generateOtp() {
        return String.format("%04d", new Random().nextInt(10000));
    }
}