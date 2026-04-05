package MaidRepository.maid.repository;

import MaidRepository.maid.model.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {

    // ✅ ADD THIS METHOD
    List<OTPVerification> findByMobile(String mobile);

    Optional<OTPVerification> findFirstByMobileAndIsVerifiedFalseOrderByCreatedAtDesc(String mobile);

    Optional<OTPVerification> findByMobileAndOtpAndIsVerifiedFalse(String mobile, String otp);

    @Modifying
    @Transactional
    void deleteByMobile(String mobile);

    @Modifying
    @Transactional
    @Query("DELETE FROM OTPVerification o WHERE o.expiryTime < :now")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);
}