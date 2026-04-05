package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.Language;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // ✅ App open pe sab available maids (filtered by user's city)
    @GetMapping("/maids")
    public ResponseEntity<APIResponse> getAllMaids(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("User authentication required"));
            }
            List<MaidResponseDTO> maids = userService.getAllAvailableMaids(mobile);
            return ResponseEntity.ok(APIResponse.success("All available maids in your city", maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maids: " + e.getMessage()));
        }
    }

    // ✅ Search maid by Service + Date + Time (filtered by user's city)
    @PostMapping("/maids/search")
    public ResponseEntity<APIResponse> searchMaids(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody MaidSearchRequestDTO request) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("User authentication required"));
            }
            List<MaidResponseDTO> maids = userService.searchMaids(request, mobile);
            return ResponseEntity.ok(APIResponse.success("Available maids found", maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error(e.getMessage()));
        }
    }

    // ✅ NEW: Advanced search with filters (top rated, nearby, budget, availability)
    @PostMapping("/user/mymaid/search")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<APIResponse> searchMaidsWithCriteria(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody MaidSearchCriteriaDTO criteria,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("User authentication required"));
            }

            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            criteria.setOffset(offset);
            criteria.setLimit(limit);

            Map<String, Object> result = userService.searchMaidsWithCriteria(user, criteria);
            return ResponseEntity.ok(APIResponse.success("Maids fetched successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Search failed: " + e.getMessage()));
        }
    }

    // ✅ Update user profile (name, email, city, profile photo)
    @PutMapping("/profile")
    public ResponseEntity<APIResponse> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UserUpdateRequestDTO request) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("User authentication required"));
            }
            UserResponseDTO updatedUser = userService.updateUserByMobile(mobile, request);
            return ResponseEntity.ok(APIResponse.success("Profile updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    // ✅ NEW: Update user languages
    @PutMapping("/languages")
    public ResponseEntity<APIResponse> updateUserLanguages(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Set<Language> languages) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("User authentication required"));
            }

            UserResponseDTO updatedUser = userService.updateUserLanguages(mobile, languages);
            return ResponseEntity.ok(APIResponse.success("Languages updated successfully", updatedUser));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update languages: " + e.getMessage()));
        }
    }

    // ✅ Helper method to extract mobile from auth header
    private String extractMobileFromAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return null;
        }

        String header = authHeader.trim();
        System.out.println("Auth Header: " + header); // Debug

        if (header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            System.out.println("Token: " + token); // Debug

            if (token.startsWith("token-")) {
                String[] parts = token.split("-");
                System.out.println("Token parts: " + String.join(",", parts)); // Debug

                if (parts.length >= 2) {
                    String mobile = parts[1];
                    System.out.println("Extracted mobile: " + mobile); // Debug
                    return mobile;
                }
            }
            return null;
        }
        else if (header.startsWith("Mobile ")) {
            String mobile = header.substring(7).trim();
            return isValidMobile(mobile) ? mobile : null;
        }
        else {
            return isValidMobile(header) ? header : null;
        }
    }

    private boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) return false;
        String cleaned = mobile.trim();
        return cleaned.matches("^[6-9]\\d{9}$");
    }
}