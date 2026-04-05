package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.ServiceType;
import MaidRepository.maid.service.MaidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paid/maids")
public class PaidMaidController {

    @Autowired
    private MaidService maidService;

    @PostMapping("/advanced-search")
    public ResponseEntity<APIResponse> advancedSearch(
            @RequestBody AdvancedSearchRequestDTO request,
            @RequestHeader("userId") Long userId) {

        try {
            Page<MaidPrivateResponseDTO> result = maidService.advancedSearch(request, userId);
            return ResponseEntity.ok(APIResponse.success("Advanced search results", result));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Advanced search failed: " + e.getMessage()));
        }
    }

    @PostMapping("/{maidId}/unlock-contact")
    public ResponseEntity<APIResponse> unlockContact(
            @PathVariable Long maidId,
            @RequestHeader("userId") Long userId) {

        try {
            UnlockContactResponseDTO result = maidService.unlockContact(maidId, userId);
            return ResponseEntity.ok(APIResponse.success("Contact unlocked", result));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to unlock contact: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse> freeSearch(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ServiceType serviceType,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("===== SEARCH CALLED =====");
        System.out.println("city: " + city);
        System.out.println("serviceType: " + serviceType);
        System.out.println("minExperience: " + minExperience);
        System.out.println("minRating: " + minRating);
        System.out.println("page: " + page);
        System.out.println("size: " + size);

        try {
            FreeSearchRequestDTO request = new FreeSearchRequestDTO();
            request.setCity(city);
            request.setServiceType(serviceType);
            request.setMinExperience(minExperience);
            request.setMinRating(minRating);
            request.setPage(page);
            request.setSize(size);

            System.out.println("Calling service with request: " + request);

            Page<MaidPublicResponseDTO> result = maidService.freeSearch(request);

            System.out.println("Result size: " + result.getTotalElements());
            System.out.println("Result content: " + result.getContent());

            return ResponseEntity.ok(APIResponse.success("Free search results", result));

        } catch (Exception e) {
            System.err.println("ERROR in search: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Search failed: " + e.getMessage()));
        }
    }
}