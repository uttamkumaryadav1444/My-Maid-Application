package MaidRepository.maid.controller;

import MaidRepository.maid.dto.APIResponse;
import MaidRepository.maid.model.User;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final MaidRepository maidRepository;

    public FileUploadController(FileStorageService fileStorageService,
                                UserRepository userRepository,
                                MaidRepository maidRepository) {
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.maidRepository = maidRepository;
    }

    // ==================== USER UPLOADS ====================

    /**
     * Upload user profile photo
     */
    @PostMapping("/user/profile-photo")
    public ResponseEntity<APIResponse> uploadUserProfilePhoto(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            String fileUrl = fileStorageService.storeFile(file, "users/" + user.getId() + "/profile");
            user.setProfilePhotoUrl(fileUrl);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("photoUrl", fileUrl);
            response.put("message", "Profile photo uploaded successfully");

            return ResponseEntity.ok(APIResponse.success("Profile photo uploaded", response));

        } catch (Exception e) {
            log.error("Failed to upload profile photo: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    /**
     * Upload user selfie for verification
     */
    @PostMapping("/user/selfie")
    public ResponseEntity<APIResponse> uploadUserSelfie(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            String fileUrl = fileStorageService.storeFile(file, "users/" + user.getId() + "/selfie");
            user.setSelfiePhotoUrl(fileUrl);
            user.setSelfieVerified(false);
            user.setVerificationStatus(User.VerificationStatus.SELFIE_SUBMITTED);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("selfieUrl", fileUrl);
            response.put("status", "PENDING_VERIFICATION");

            return ResponseEntity.ok(APIResponse.success("Selfie uploaded for verification", response));

        } catch (Exception e) {
            log.error("Failed to upload selfie: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    /**
     * Upload user Aadhar document
     */
    @PostMapping("/user/aadhar")
    public ResponseEntity<APIResponse> uploadUserAadhar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file,
            @RequestParam("aadharNumber") String aadharNumber) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            String fileUrl = fileStorageService.storeFile(file, "users/" + user.getId() + "/documents");
            user.setAadharNumber(aadharNumber);
            user.setAadharPhotoUrl(fileUrl);
            user.setAadharVerified(false);
            user.setVerificationStatus(User.VerificationStatus.DOCUMENTS_SUBMITTED);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("aadharUrl", fileUrl);
            response.put("aadharNumber", aadharNumber);
            response.put("status", "PENDING_VERIFICATION");

            return ResponseEntity.ok(APIResponse.success("Aadhar uploaded for verification", response));

        } catch (Exception e) {
            log.error("Failed to upload Aadhar: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    /**
     * Upload user PAN document
     */
    @PostMapping("/user/pan")
    public ResponseEntity<APIResponse> uploadUserPan(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file,
            @RequestParam("panNumber") String panNumber) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            String fileUrl = fileStorageService.storeFile(file, "users/" + user.getId() + "/documents");
            user.setPanNumber(panNumber);
            user.setPanPhotoUrl(fileUrl);
            user.setPanVerified(false);
            user.setVerificationStatus(User.VerificationStatus.DOCUMENTS_SUBMITTED);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("panUrl", fileUrl);
            response.put("panNumber", panNumber);
            response.put("status", "PENDING_VERIFICATION");

            return ResponseEntity.ok(APIResponse.success("PAN uploaded for verification", response));

        } catch (Exception e) {
            log.error("Failed to upload PAN: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    // ==================== MAID UPLOADS ====================

    /**
     * Upload maid profile photo
     */
    @PostMapping("/maid/profile-photo")
    public ResponseEntity<APIResponse> uploadMaidProfilePhoto(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            Maid maid = maidRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            String fileUrl = fileStorageService.storeFile(file, "maids/" + maid.getId() + "/profile");
            maid.setProfilePhotoUrl(fileUrl);
            maidRepository.save(maid);

            Map<String, Object> response = new HashMap<>();
            response.put("photoUrl", fileUrl);
            response.put("message", "Profile photo uploaded successfully");

            return ResponseEntity.ok(APIResponse.success("Profile photo uploaded", response));

        } catch (Exception e) {
            log.error("Failed to upload profile photo: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    /**
     * Upload maid selfie for verification
     */
    @PostMapping("/maid/selfie")
    public ResponseEntity<APIResponse> uploadMaidSelfie(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            Maid maid = maidRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            String fileUrl = fileStorageService.storeFile(file, "maids/" + maid.getId() + "/selfie");
            maid.setSelfiePhotoUrl(fileUrl);
            maid.setSelfieVerified(false);
            maid.setVerificationStatus(Maid.VerificationStatus.SELFIE_SUBMITTED);
            maidRepository.save(maid);

            Map<String, Object> response = new HashMap<>();
            response.put("selfieUrl", fileUrl);
            response.put("status", "PENDING_VERIFICATION");

            return ResponseEntity.ok(APIResponse.success("Selfie uploaded for verification", response));

        } catch (Exception e) {
            log.error("Failed to upload selfie: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    /**
     * Upload maid Aadhar document
     */
    @PostMapping("/maid/aadhar")
    public ResponseEntity<APIResponse> uploadMaidAadhar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file,
            @RequestParam("aadharNumber") String aadharNumber) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            Maid maid = maidRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            String fileUrl = fileStorageService.storeFile(file, "maids/" + maid.getId() + "/documents");
            maid.setAadharNumber(aadharNumber);
            maid.setAadharPhotoUrl(fileUrl);
            maid.setAadharVerified(false);
            maid.setVerificationStatus(Maid.VerificationStatus.DOCUMENTS_SUBMITTED);
            maidRepository.save(maid);

            Map<String, Object> response = new HashMap<>();
            response.put("aadharUrl", fileUrl);
            response.put("aadharNumber", aadharNumber);
            response.put("status", "PENDING_VERIFICATION");

            return ResponseEntity.ok(APIResponse.success("Aadhar uploaded for verification", response));

        } catch (Exception e) {
            log.error("Failed to upload Aadhar: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    /**
     * Upload maid PAN document
     */
    @PostMapping("/maid/pan")
    public ResponseEntity<APIResponse> uploadMaidPan(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file,
            @RequestParam("panNumber") String panNumber) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            Maid maid = maidRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            String fileUrl = fileStorageService.storeFile(file, "maids/" + maid.getId() + "/documents");
            maid.setPanNumber(panNumber);
            maid.setPanPhotoUrl(fileUrl);
            maid.setPanVerified(false);
            maid.setVerificationStatus(Maid.VerificationStatus.DOCUMENTS_SUBMITTED);
            maidRepository.save(maid);

            Map<String, Object> response = new HashMap<>();
            response.put("panUrl", fileUrl);
            response.put("panNumber", panNumber);
            response.put("status", "PENDING_VERIFICATION");

            return ResponseEntity.ok(APIResponse.success("PAN uploaded for verification", response));

        } catch (Exception e) {
            log.error("Failed to upload PAN: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    // Helper method
    private String extractMobileFromAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return null;
        }

        String header = authHeader.trim();

        if (header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            if (token.startsWith("token-")) {
                String[] parts = token.split("-");
                if (parts.length >= 2) {
                    return parts[1];
                }
            }
            return null;
        } else if (header.startsWith("Mobile ")) {
            return header.substring(7).trim();
        } else {
            return header;
        }
    }
}