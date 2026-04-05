package MaidRepository.maid.repository;

import MaidRepository.maid.model.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {

    // ✅ String use karo, enum nahi
    Optional<OTPVerification> findTopByMobileAndUserTypeOrderByCreatedAtDesc(
            String mobile, String userType);  // String parameter

    Optional<OTPVerification> findByMobileAndOtpAndIsVerifiedFalse(
            String mobile, String otp);

    void deleteByMobile(String mobile);
}