package MaidRepository.maid.controller;

import MaidRepository.maid.dto.APIResponse;
import MaidRepository.maid.model.User;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.repository.MaidRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/verify")
public class VerificationController {

    private final UserRepository userRepository;
    private final MaidRepository maidRepository;

    public VerificationController(UserRepository userRepository, MaidRepository maidRepository) {
        this.userRepository = userRepository;
        this.maidRepository = maidRepository;
    }

    /**
     * Verify user's selfie
     */
    @PostMapping("/user/{userId}/selfie")
    public ResponseEntity<APIResponse> verifyUserSelfie(
            @PathVariable Long userId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remarks) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setSelfieVerified(approved);
            user.setSelfieVerifiedAt(LocalDateTime.now());

            if (approved) {
                if (user.getAadharVerified() && user.getPanVerified()) {
                    user.setVerificationStatus(User.VerificationStatus.VERIFIED);
                } else {
                    user.setVerificationStatus(User.VerificationStatus.DOCUMENTS_SUBMITTED);
                }
            } else {
                user.setVerificationStatus(User.VerificationStatus.REJECTED);
            }

            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("selfieVerified", approved);
            response.put("status", user.getVerificationStatus());

            return ResponseEntity.ok(APIResponse.success("Selfie verification updated", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Verification failed: " + e.getMessage()));
        }
    }

    /**
     * Verify user's Aadhar
     */
    @PostMapping("/user/{userId}/aadhar")
    public ResponseEntity<APIResponse> verifyUserAadhar(
            @PathVariable Long userId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remarks) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setAadharVerified(approved);

            if (approved && user.getSelfieVerified() && user.getPanVerified()) {
                user.setVerificationStatus(User.VerificationStatus.VERIFIED);
            }

            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("aadharVerified", approved);

            return ResponseEntity.ok(APIResponse.success("Aadhar verification updated", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Verification failed: " + e.getMessage()));
        }
    }

    /**
     * Verify user's PAN
     */
    @PostMapping("/user/{userId}/pan")
    public ResponseEntity<APIResponse> verifyUserPan(
            @PathVariable Long userId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remarks) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setPanVerified(approved);

            if (approved && user.getSelfieVerified() && user.getAadharVerified()) {
                user.setVerificationStatus(User.VerificationStatus.VERIFIED);
            }

            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("panVerified", approved);

            return ResponseEntity.ok(APIResponse.success("PAN verification updated", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Verification failed: " + e.getMessage()));
        }
    }

    // Similar methods for Maid verification
    @PostMapping("/maid/{maidId}/selfie")
    public ResponseEntity<APIResponse> verifyMaidSelfie(
            @PathVariable Long maidId,
            @RequestParam boolean approved) {
        try {
            Maid maid = maidRepository.findById(maidId)
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            maid.setSelfieVerified(approved);
            maid.setSelfieVerifiedAt(LocalDateTime.now());

            if (approved && maid.getAadharVerified() && maid.getPanVerified()) {
                maid.setVerificationStatus(Maid.VerificationStatus.VERIFIED);
            }

            maidRepository.save(maid);

            return ResponseEntity.ok(APIResponse.success("Maid selfie verification updated", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Verification failed: " + e.getMessage()));
        }
    }
}