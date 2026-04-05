package MaidRepository.maid.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    /**
     * Extract username (mobile) from token
     * Token format: token-9876543210-1743157123456-abc123
     */
    public String getUsernameFromToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.warn("Token is null or empty");
                return null;
            }

            // Check if token starts with "token-"
            if (token.startsWith("token-")) {
                String[] parts = token.split("-");
                if (parts.length >= 2) {
                    String mobile = parts[1];
                    log.debug("Extracted mobile from token: {}", mobile);
                    return mobile;
                }
            }

            log.warn("Token format invalid: {}", token);
            return null;

        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }

            // Check if token starts with "token-"
            if (!token.startsWith("token-")) {
                log.warn("Token doesn't start with token-");
                return false;
            }

            String[] parts = token.split("-");
            if (parts.length < 2) {
                log.warn("Invalid token format");
                return false;
            }

            // You can add more validation here (expiry, etc.)
            log.debug("Token validated successfully");
            return true;

        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }
}