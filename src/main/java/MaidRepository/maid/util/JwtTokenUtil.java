package MaidRepository.maid.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret:MySuperSecretKeyForJWTTokenGenerationInMaidRepositoryApplication2024}")
    private String secretString;

    private Key key;
    private final long expiration = 86400000; // 24 hours

    // Using @PostConstruct is correct, but let's initialize in constructor too
    public JwtTokenUtil() {
        initializeKey();
    }

    private void initializeKey() {
        try {
            // Check if secret is Base64 encoded
            if (secretString == null || secretString.trim().isEmpty()) {
                secretString = "MySuperSecretKeyForJWTTokenGenerationInMaidRepositoryApplication2024";
            }

            // Try to decode as Base64 first
            try {
                byte[] keyBytes = java.util.Base64.getDecoder().decode(secretString);
                this.key = Keys.hmacShaKeyFor(keyBytes);
                System.out.println("JWT: Using Base64 encoded secret");
            } catch (IllegalArgumentException e) {
                // If not Base64, use as plain string
                byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
                // Ensure key length is at least 256 bits (32 bytes) for HS256
                if (keyBytes.length < 32) {
                    // Pad with zeros or repeat
                    byte[] padded = new byte[32];
                    System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
                    this.key = Keys.hmacShaKeyFor(padded);
                } else {
                    this.key = Keys.hmacShaKeyFor(keyBytes);
                }
                System.out.println("JWT: Using plain text secret");
            }
            System.out.println("JWT: Key initialized successfully");
        } catch (Exception e) {
            System.err.println("JWT: Error initializing key: " + e.getMessage());
            throw new RuntimeException("Failed to initialize JWT key", e);
        }
    }

    public String generateToken(String username, String userType, Long userId) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userType", userType);
            claims.put("userId", userId);

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            System.err.println("JWT: Error generating token: " + e.getMessage());
            throw e;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            System.err.println("JWT: Error extracting username: " + e.getMessage());
            return null;
        }
    }

    public String getUserTypeFromToken(String token) {
        try {
            return (String) Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userType");
        } catch (Exception e) {
            System.err.println("JWT: Error extracting userType: " + e.getMessage());
            return null;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Object userId = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId");
            return userId != null ? Long.valueOf(userId.toString()) : null;
        } catch (Exception e) {
            System.err.println("JWT: Error extracting userId: " + e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("JWT: Token has expired");
            return false;
        } catch (Exception e) {
            System.err.println("JWT: Invalid token - " + e.getMessage());
            return false;
        }
    }
}