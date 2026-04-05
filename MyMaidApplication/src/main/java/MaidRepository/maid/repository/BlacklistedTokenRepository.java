package MaidRepository.maid.repository;

import MaidRepository.maid.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    // ✅ Check if token exists (used in logout)
    boolean existsByToken(String token);

    // ✅ Find by token (optional)
    Optional<BlacklistedToken> findByToken(String token);

    // ✅ Delete expired tokens (you can add this later)
    // void deleteByExpiryTimeBefore(LocalDateTime time);
}