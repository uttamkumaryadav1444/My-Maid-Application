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

public interface OtpRepository extends JpaRepository<OTPVerification, Long> {

    // ✅ YEH METHOD ADD KARO
    List<OTPVerification> findByMobile(String mobile);

    Optional<OTPVerification> findFirstByMobileAndUserTypeAndIsVerifiedFalseOrderByCreatedAtDesc(
            String mobile, String userType);

    Optional<OTPVerification> findByMobileAndOtpAndIsVerifiedFalse(
            String mobile, String otp);

    void deleteByMobile(String mobile);

    @Modifying
    @Transactional
    @Query("DELETE FROM OTPVerification o WHERE o.mobile = :mobile AND o.purpose = :purpose")
    void deleteByMobileAndPurpose(@Param("mobile") String mobile, @Param("purpose") String purpose);

    @Modifying
    @Transactional
    @Query("DELETE FROM OTPVerification o WHERE o.expiryTime < :now")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);
}