package MaidRepository.maid.impl;

import MaidRepository.maid.dto.Msg91OtpResponseDTO;
import MaidRepository.maid.model.OTPVerification;
import MaidRepository.maid.repository.OTPVerificationRepository;
import MaidRepository.maid.service.Msg91Service;
import MaidRepository.maid.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);

    private final Msg91Service msg91Service;
    private final OTPVerificationRepository otpRepository;

    @Value("${otp.expiry.minutes:10}")
    private int otpExpiryMinutes;

    @Value("${otp.test.mode:false}")
    private boolean testMode;

    @Value("${otp.length:6}")
    private int otpLength;

    public OtpServiceImpl(Msg91Service msg91Service, OTPVerificationRepository otpRepository) {
        this.msg91Service = msg91Service;
        this.otpRepository = otpRepository;
    }

    @Override
    public boolean isTestMode() {
        return testMode;
    }

    @Override
    @Transactional
    public Msg91OtpResponseDTO sendOtp(String mobile) {
        try {
            log.info("Sending OTP to mobile: {}, testMode: {}", mobile, testMode);

            if (mobile == null || !mobile.matches("^[6-9]\\d{9}$")) {
                throw new IllegalArgumentException("Invalid mobile number");
            }

            // Delete old OTPs
            otpRepository.deleteByMobile(mobile);

            Msg91OtpResponseDTO response;

            if (testMode) {
                // ✅ TEST MODE - Generate and store OTP
                String generatedOtp = generateOtp();
                log.info("🔧 TEST MODE: OTP for {} is: {}", mobile, generatedOtp);

                System.err.println("\n🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐");
                System.err.println("🔐   OTP: " + generatedOtp);
                System.err.println("🔐   Mobile: " + mobile);
                System.err.println("🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐🔐\n");

                response = new Msg91OtpResponseDTO();
                response.setType("success");
                response.setMessage("OTP sent successfully");
                response.setRequestId("TEST_" + System.currentTimeMillis());
                response.setOtp(generatedOtp);

                // ✅ Use factory method for test OTP
                OTPVerification otpEntity = OTPVerification.createForTestOTP(
                        mobile,
                        generatedOtp,
                        response.getRequestId(),
                        LocalDateTime.now().plusMinutes(otpExpiryMinutes)
                );
                otpRepository.save(otpEntity);

                return response;
            }

            // ✅ REAL MODE - Send via MSG91
            response = msg91Service.sendOtp(mobile);
            log.info("MSG91 Response: {}", response);

            if (response != null && "success".equalsIgnoreCase(response.getType())) {
                // ✅ Use factory method for real OTP (otp = null)
                OTPVerification otpEntity = OTPVerification.createForRealOTP(
                        mobile,
                        response.getRequestId(),
                        LocalDateTime.now().plusMinutes(otpExpiryMinutes)
                );
                otpRepository.save(otpEntity);
                log.info("✅ Real OTP sent via MSG91 to {}", mobile);
            } else {
                log.error("❌ Failed to send OTP via MSG91: {}", response);
                throw new RuntimeException("Failed to send OTP");
            }

            return response;

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to send OTP: {}", e.getMessage());
            throw new RuntimeException("OTP send failed: " + e.getMessage());
        }
    }

    @Override
    public String generateAndSendOTP(String mobile, String email) {
        Msg91OtpResponseDTO response = sendOtp(mobile);
        return response != null ? response.getRequestId() : null;
    }

    @Override
    @Transactional
    public boolean verifyOTP(String mobile, String otp) {
        try {
            log.info("Verifying OTP for mobile: {}", mobile);

            if (mobile == null || otp == null) {
                return false;
            }

            List<OTPVerification> otpList = otpRepository.findByMobile(mobile);

            if (otpList.isEmpty()) {
                log.warn("No OTP record found for mobile: {}", mobile);
                return false;
            }

            // Get the latest unverified OTP
            OTPVerification otpEntity = otpList.stream()
                    .filter(o -> !o.getIsVerified())
                    .findFirst()
                    .orElse(null);

            if (otpEntity == null) {
                log.warn("No unverified OTP found for mobile: {}", mobile);
                return false;
            }

            // Check expiry
            if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
                log.warn("OTP expired for mobile: {}", mobile);
                return false;
            }

            boolean isVerified = false;

            if (testMode) {
                // ✅ TEST MODE - Check against stored OTP
                if (otpEntity.getOtp() != null && otpEntity.getOtp().equals(otp)) {
                    isVerified = true;
                    log.info("✅ Test mode OTP verified for {}", mobile);
                }
            } else {
                // ✅ REAL MODE - Verify with MSG91 API
                try {
                    Msg91OtpResponseDTO verificationResponse = msg91Service.verifyOtp(mobile, otp);

                    if (verificationResponse != null && "success".equalsIgnoreCase(verificationResponse.getType())) {
                        isVerified = true;
                        log.info("✅ Real OTP verified via MSG91 for {}", mobile);
                    } else {
                        log.warn("❌ MSG91 verification failed for {}: {}", mobile, verificationResponse);
                    }
                } catch (Exception e) {
                    log.error("Error verifying OTP with MSG91: {}", e.getMessage());
                }
            }

            if (isVerified) {
                otpEntity.setIsVerified(true);
                otpEntity.setVerifiedAt(LocalDateTime.now());
                otpRepository.save(otpEntity);
                return true;
            }

            // Increment attempt count
            otpEntity.setAttemptCount(otpEntity.getAttemptCount() + 1);
            otpRepository.save(otpEntity);

            log.warn("❌ Invalid OTP for {}: {}", mobile, otp);
            return false;

        } catch (Exception e) {
            log.error("OTP verification failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void resendOTP(String mobile) {
        try {
            log.info("Resending OTP for mobile: {}", mobile);

            if (mobile == null || !mobile.matches("^[6-9]\\d{9}$")) {
                throw new IllegalArgumentException("Invalid mobile number");
            }

            otpRepository.deleteByMobile(mobile);

            if (testMode) {
                String newOtp = generateOtp();
                log.info("🔧 TEST MODE: New OTP: {}", newOtp);

                OTPVerification otpEntity = OTPVerification.createForTestOTP(
                        mobile,
                        newOtp,
                        "TEST_" + System.currentTimeMillis(),
                        LocalDateTime.now().plusMinutes(otpExpiryMinutes)
                );
                otpRepository.save(otpEntity);

                System.err.println("🔄 New OTP: " + newOtp);
                return;
            }

            // ✅ REAL MODE - Resend via MSG91
            Msg91OtpResponseDTO response = msg91Service.resendOtp(mobile);

            if (response != null && "success".equalsIgnoreCase(response.getType())) {
                OTPVerification otpEntity = OTPVerification.createForRealOTP(
                        mobile,
                        response.getRequestId(),
                        LocalDateTime.now().plusMinutes(otpExpiryMinutes)
                );
                otpRepository.save(otpEntity);
                log.info("✅ Real OTP resent via MSG91 to {}", mobile);
            } else {
                throw new RuntimeException("Failed to resend OTP");
            }

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to resend OTP: {}", e.getMessage());
            throw new RuntimeException("Failed to resend OTP");
        }
    }

    @Override
    @Transactional
    public void deleteOtp(String mobile) {
        try {
            log.info("Deleting OTPs for mobile: {}", mobile);
            otpRepository.deleteByMobile(mobile);
        } catch (Exception e) {
            log.error("Failed to delete OTP: {}", e.getMessage());
        }
    }

    @Override
    public String generateOtp() {
        int max = (int) Math.pow(10, otpLength) - 1;
        int min = (int) Math.pow(10, otpLength - 1);
        int otpInt = new Random().nextInt(max - min + 1) + min;
        return String.valueOf(otpInt);
    }
}