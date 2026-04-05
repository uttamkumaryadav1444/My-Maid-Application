package MaidRepository.maid.util;

import MaidRepository.maid.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        // Skip for public endpoints (to avoid unnecessary logs)
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth/") ||
                requestURI.startsWith("/api/otp/") ||
                requestURI.startsWith("/api/health") ||
                requestURI.startsWith("/api/ping") ||
                requestURI.startsWith("/api/public/")) {
            chain.doFilter(request, response);
            return;
        }

        log.debug("Processing request: {} - Authorization: {}", requestURI, requestTokenHeader);

        String username = null;
        String jwtToken = null;

        // JWT Token is in the form "Bearer token-8969161910-1772902494059"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            log.debug("Extracted token: {}", jwtToken);

            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                log.debug("Extracted username from token: {}", username);
            } catch (Exception e) {
                log.error("Unable to get username from token: {}", e.getMessage());
            }
        } else {
            log.warn("JWT Token does not begin with Bearer String or header is null");
        }

        // Once we get the token, validate it
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                log.debug("Loaded user details for: {}", username);

                // If token is valid configure Spring Security
                if (jwtTokenUtil.validateToken(jwtToken)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    log.info("✅ User authenticated successfully: {}", username);
                } else {
                    log.warn("❌ Token validation failed for user: {}", username);
                }
            } catch (Exception e) {
                log.error("Error loading user: {}", e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}