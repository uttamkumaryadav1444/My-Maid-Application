package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.service.MaidService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maids")
public class MaidController {

    private final MaidService maidService;
    private final MaidRepository maidRepository;

    public MaidController(MaidService maidService, MaidRepository maidRepository) {
        this.maidService = maidService;
        this.maidRepository = maidRepository;
    }

    // ✅ GET ALL MAIDS
    @GetMapping("/all")
    public ResponseEntity<APIResponse> getAllMaids() {
        try {
            List<MaidResponseDTO> maids = maidService.getAllMaids();
            return ResponseEntity.ok(APIResponse.success("Maids fetched successfully", maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maids: " + e.getMessage()));
        }
    }

    // ✅ GET MAID BY ID
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getMaidById(@PathVariable Long id) {
        try {
            MaidResponseDTO maid = maidService.getMaidById(id);
            return ResponseEntity.ok(APIResponse.success("Maid found", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Maid not found: " + e.getMessage()));
        }
    }

    // ✅ UPDATE MAID (BASIC)
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> updateMaid(
            @PathVariable Long id,
            @RequestBody MaidUpdateRequestDTO request) {
        try {
            MaidResponseDTO updatedMaid = maidService.updateMaid(id, request);
            return ResponseEntity.ok(APIResponse.success("Maid updated", updatedMaid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update maid: " + e.getMessage()));
        }
    }

    // ✅ DELETE MAID
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteMaid(@PathVariable Long id) {
        try {
            maidService.deleteMaid(id);
            return ResponseEntity.ok(APIResponse.success("Maid deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to delete maid: " + e.getMessage()));
        }
    }

    // ✅ GET MAID BY MOBILE
    @GetMapping("/mobile/{mobile}")
    public ResponseEntity<APIResponse> getMaidByMobile(@PathVariable String mobile) {
        try {
            MaidResponseDTO maid = maidService.getMaidByMobile(mobile);
            return ResponseEntity.ok(APIResponse.success("Maid found by mobile", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Maid not found: " + e.getMessage()));
        }
    }

    // ✅ GET MAID BY EMAIL
    @GetMapping("/email/{email}")
    public ResponseEntity<APIResponse> getMaidByEmail(@PathVariable String email) {
        try {
            MaidResponseDTO maid = maidService.getMaidByEmail(email);
            return ResponseEntity.ok(APIResponse.success("Maid found by email", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Maid not found: " + e.getMessage()));
        }
    }

    // ✅ SEARCH & FILTER APIs

    // GET MAIDS BY SERVICE TYPE
    @GetMapping("/service/{serviceType}")
    public ResponseEntity<APIResponse> getMaidsByServiceType(@PathVariable String serviceType) {
        try {
            List<MaidResponseDTO> maids = maidService.getMaidsByServiceType(serviceType);
            return ResponseEntity.ok(APIResponse.success("Maids with service: " + serviceType, maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maids: " + e.getMessage()));
        }
    }

    // GET MAIDS BY CITY
    @GetMapping("/city/{city}")
    public ResponseEntity<APIResponse> getMaidsByCity(@PathVariable String city) {
        try {
            List<MaidResponseDTO> maids = maidService.getMaidsByCity(city);
            return ResponseEntity.ok(APIResponse.success("Maids in city: " + city, maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maids: " + e.getMessage()));
        }
    }

    // GET MAIDS BY CITY AND SERVICE TYPE
    @GetMapping("/city-service")
    public ResponseEntity<APIResponse> getMaidsByCityAndServiceType(
            @RequestParam String city,
            @RequestParam String serviceType) {
        try {
            List<MaidResponseDTO> maids = maidService.getMaidsByCityAndServiceType(city, serviceType);
            return ResponseEntity.ok(APIResponse.success(
                    "Maids in " + city + " with service: " + serviceType, maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maids: " + e.getMessage()));
        }
    }

    // SEARCH MAIDS BY NAME
    @GetMapping("/search/name")
    public ResponseEntity<APIResponse> searchMaidsByName(@RequestParam String name) {
        try {
            List<MaidResponseDTO> maids = maidService.searchMaidsByName(name);
            return ResponseEntity.ok(APIResponse.success("Maids found with name: " + name, maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Search failed: " + e.getMessage()));
        }
    }

    // GET MAIDS BY SKILL
    @GetMapping("/skill/{skill}")
    public ResponseEntity<APIResponse> getMaidsBySkill(@PathVariable String skill) {
        try {
            List<MaidResponseDTO> maids = maidService.getMaidsBySkill(skill);
            return ResponseEntity.ok(APIResponse.success("Maids with skill: " + skill, maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maids: " + e.getMessage()));
        }
    }

    // GET ACTIVE MAIDS
    @GetMapping("/active")
    public ResponseEntity<APIResponse> getActiveMaids() {
        try {
            List<MaidResponseDTO> maids = maidService.getActiveMaids();
            return ResponseEntity.ok(APIResponse.success("Active maids", maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch active maids: " + e.getMessage()));
        }
    }

    // GET VERIFIED MAIDS
    @GetMapping("/verified")
    public ResponseEntity<APIResponse> getVerifiedMaids() {
        try {
            List<MaidResponseDTO> maids = maidService.getVerifiedMaids();
            return ResponseEntity.ok(APIResponse.success("Verified maids", maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch verified maids: " + e.getMessage()));
        }
    }

    // GET MAIDS BY EXPERIENCE (MINIMUM)
    @GetMapping("/experience")
    public ResponseEntity<APIResponse> getMaidsByMinExperience(@RequestParam Integer minExperience) {
        try {
            List<MaidResponseDTO> maids = maidService.getMaidsByMinExperience(minExperience);
            return ResponseEntity.ok(APIResponse.success(
                    "Maids with experience >= " + minExperience + " years", maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maids: " + e.getMessage()));
        }
    }

    // GET MAIDS BY HOURLY RATE RANGE
    @GetMapping("/hourly-rate")
    public ResponseEntity<APIResponse> getMaidsByHourlyRateRange(
            @RequestParam Double minRate,
            @RequestParam Double maxRate) {
        try {
            List<MaidResponseDTO> maids = maidService.getMaidsByHourlyRateRange(minRate, maxRate);
            return ResponseEntity.ok(APIResponse.success(
                    "Maids with hourly rate between " + minRate + " and " + maxRate, maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maids: " + e.getMessage()));
        }
    }

    // ADVANCED SEARCH WITH MULTIPLE PARAMETERS
    @GetMapping("/advanced-search")
    public ResponseEntity<APIResponse> advancedSearch(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Double maxHourlyRate,
            @RequestParam(required = false) Boolean isAvailable) {
        try {
            List<MaidResponseDTO> maids = maidService.advancedSearch(
                    city, serviceType, minExperience, maxHourlyRate, isAvailable);
            return ResponseEntity.ok(APIResponse.success("Search results", maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Search failed: " + e.getMessage()));
        }
    }

    // SORT MAIDS
    @GetMapping("/sort")
    public ResponseEntity<APIResponse> sortMaids(
            @RequestParam String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            List<MaidResponseDTO> maids = maidService.sortMaids(sortBy, direction);
            return ResponseEntity.ok(APIResponse.success("Sorted maids by " + sortBy, maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Sorting failed: " + e.getMessage()));
        }
    }

    // PAGINATED MAIDS
    @GetMapping("/paginated")
    public ResponseEntity<APIResponse> getPaginatedMaids(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Page<MaidResponseDTO> maids = maidService.getPaginatedMaids(page, size, sortBy, direction);
            return ResponseEntity.ok(APIResponse.success("Paginated maids", maids));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch paginated maids: " + e.getMessage()));
        }
    }

    // ✅ PROFILE UPDATE APIs

    // UPDATE MAID AVAILABILITY
    @PutMapping("/{id}/availability")
    public ResponseEntity<APIResponse> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean available) {
        try {
            MaidResponseDTO maid = maidService.updateMaidAvailability(id, available);
            return ResponseEntity.ok(APIResponse.success("Availability updated", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update availability: " + e.getMessage()));
        }
    }

    // UPDATE MAID HOURLY RATE
    @PutMapping("/{id}/hourly-rate")
    public ResponseEntity<APIResponse> updateHourlyRate(
            @PathVariable Long id,
            @RequestParam Double hourlyRate) {
        try {
            MaidResponseDTO maid = maidService.updateMaidHourlyRate(id, hourlyRate);
            return ResponseEntity.ok(APIResponse.success("Hourly rate updated", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update hourly rate: " + e.getMessage()));
        }
    }

    // UPDATE MAID STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<APIResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            MaidResponseDTO maid = maidService.updateMaidStatus(id, status);
            return ResponseEntity.ok(APIResponse.success("Status updated", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update status: " + e.getMessage()));
        }
    }

    // UPDATE EXPERIENCE & SKILLS
    @PutMapping("/{id}/experience-skills")
    public ResponseEntity<APIResponse> updateExperienceAndSkills(
            @PathVariable Long id,
            @RequestParam Integer experience,
            @RequestParam String skills) {
        try {
            MaidResponseDTO maid = maidService.updateExperienceAndSkills(id, experience, skills);
            return ResponseEntity.ok(APIResponse.success("Experience and skills updated", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update: " + e.getMessage()));
        }
    }

    // ✅ DOCUMENT & PHOTO MANAGEMENT

    // UPDATE PROFILE PHOTO
    @PutMapping("/{id}/profile-photo")
    public ResponseEntity<APIResponse> updateProfilePhoto(
            @PathVariable Long id,
            @RequestParam String photoUrl) {
        try {
            MaidResponseDTO maid = maidService.updateProfilePhoto(id, photoUrl);
            return ResponseEntity.ok(APIResponse.success("Profile photo updated", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update photo: " + e.getMessage()));
        }
    }

    // ADD DOCUMENT
    @PostMapping("/{id}/documents")
    public ResponseEntity<APIResponse> addDocument(
            @PathVariable Long id,
            @RequestParam String documentUrl,
            @RequestParam String documentType) {
        try {
            MaidResponseDTO maid = maidService.addDocument(id, documentUrl, documentType);
            return ResponseEntity.ok(APIResponse.success("Document added", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to add document: " + e.getMessage()));
        }
    }

    // DELETE PROFILE PHOTO
    @DeleteMapping("/{id}/profile-photo")
    public ResponseEntity<APIResponse> deleteProfilePhoto(@PathVariable Long id) {
        try {
            MaidResponseDTO maid = maidService.deleteProfilePhoto(id);
            return ResponseEntity.ok(APIResponse.success("Profile photo deleted", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to delete photo: " + e.getMessage()));
        }
    }

    // REMOVE DOCUMENT
    @DeleteMapping("/{id}/documents")
    public ResponseEntity<APIResponse> removeDocument(
            @PathVariable Long id,
            @RequestParam String documentUrl) {
        try {
            MaidResponseDTO maid = maidService.removeDocument(id, documentUrl);
            return ResponseEntity.ok(APIResponse.success("Document removed", maid));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to remove document: " + e.getMessage()));
        }
    }

    // GET MAID DOCUMENTS
    @GetMapping("/{id}/documents")
    public ResponseEntity<APIResponse> getMaidDocuments(@PathVariable Long id) {
        try {
            List<MaidDocumentDTO> documents = maidService.getMaidDocuments(id);
            return ResponseEntity.ok(APIResponse.success("Maid documents", documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch documents: " + e.getMessage()));
        }
    }

    // ✅ COMPLETE PROFILE EDIT
    @PutMapping("/{id}/edit-profile")
    public ResponseEntity<APIResponse> editMaidProfile(
            @PathVariable Long id,
            @RequestBody MaidUpdateRequestDTO request) {
        try {
            // Get existing maid
            Maid maid = maidRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

            // Update all fields if provided
            if (request.getName() != null) maid.setName(request.getName());
            if (request.getGender() != null) maid.setGender(request.getGender());
            if (request.getDob() != null) maid.setDob(request.getDob());
            if (request.getEmail() != null) maid.setEmail(request.getEmail());
            if (request.getCity() != null) maid.setCity(request.getCity());
            if (request.getLocality() != null) maid.setLocality(request.getLocality());
            if (request.getPincode() != null) maid.setPincode(request.getPincode());
            if (request.getServiceType() != null) maid.setServiceType(request.getServiceType());
            if (request.getSkills() != null) maid.setSkills(request.getSkills());
            if (request.getExperience() != null) maid.setExperience(request.getExperience());
            if (request.getHourlyRate() != null) maid.setHourlyRate(request.getHourlyRate());
            if (request.getStatus() != null) maid.setStatus(request.getStatus());
            if (request.getIsAvailable() != null) maid.setIsAvailable(request.getIsAvailable());

            Maid updatedMaid = maidRepository.save(maid);
            MaidResponseDTO response = convertToMaidResponseDTO(updatedMaid);

            return ResponseEntity.ok(APIResponse.success("Profile updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    // Helper method
    private MaidResponseDTO convertToMaidResponseDTO(Maid maid) {
        MaidResponseDTO dto = new MaidResponseDTO();
        dto.setId(maid.getId());
        dto.setName(maid.getName());
        dto.setGender(maid.getGender());
        dto.setDob(maid.getDob());
        dto.setMobile(maid.getMobile());
        dto.setEmail(maid.getEmail());
        dto.setServiceType(maid.getServiceType());
        dto.setSkills(maid.getSkills());
        dto.setExperience(maid.getExperience());
        dto.setCity(maid.getCity());
        dto.setLocality(maid.getLocality());
        dto.setPincode(maid.getPincode());
        dto.setLatitude(maid.getLatitude());
        dto.setLongitude(maid.getLongitude());
        dto.setProfilePhotoUrl(maid.getProfilePhotoUrl());
        dto.setStatus(maid.getStatus());
        dto.setIsVerified(maid.getIsVerified());
        dto.setIsAvailable(maid.getIsAvailable());
        dto.setAverageRating(maid.getAverageRating());
        dto.setHourlyRate(maid.getHourlyRate());
        dto.setCreatedAt(maid.getCreatedAt());
        dto.setUpdatedAt(maid.getUpdatedAt());
        return dto;
    }
}