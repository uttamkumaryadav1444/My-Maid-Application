package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.ServiceType;
import MaidRepository.maid.service.MaidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/maids")
public class PublicMaidController {

    @Autowired
    private MaidService maidService;

    @GetMapping("/search")
    public ResponseEntity<APIResponse> freeSearch(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ServiceType serviceType,  // ✅ Fixed: String to ServiceType
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            FreeSearchRequestDTO request = new FreeSearchRequestDTO();
            request.setCity(city);
            request.setServiceType(serviceType);  // ✅ Now works with ServiceType enum
            request.setMinExperience(minExperience);
            request.setMinRating(minRating);
            request.setPage(page);
            request.setSize(size);

            Page<MaidPublicResponseDTO> result = maidService.freeSearch(request);
            return ResponseEntity.ok(APIResponse.success("Free search results", result));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Search failed: " + e.getMessage()));
        }
    }

    @GetMapping("/maid/{id}")
    public ResponseEntity<APIResponse> getMaidPublicProfile(@PathVariable Long id) {
        try {
            MaidResponseDTO maid = maidService.getMaidById(id);

            // Create public profile response (without contact info)
            MaidPublicResponseDTO publicProfile = new MaidPublicResponseDTO();
            publicProfile.setId(maid.getId());
            publicProfile.setName(maid.getName());
            publicProfile.setServiceType(maid.getServiceType());
            publicProfile.setExperience(maid.getExperience());
            publicProfile.setAverageRating(maid.getAverageRating());
            publicProfile.setCity(maid.getCity());
            publicProfile.setLocality(maid.getLocality());
            publicProfile.setProfilePhotoUrl(maid.getProfilePhotoUrl());
            publicProfile.setIsAvailable(maid.getIsAvailable());
            publicProfile.setHourlyRate(maid.getHourlyRate());
            publicProfile.setLanguages(maid.getLanguages());

            // ❌ Contact info intentionally excluded

            return ResponseEntity.ok(APIResponse.success("Maid public profile", publicProfile));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch maid: " + e.getMessage()));
        }
    }
}