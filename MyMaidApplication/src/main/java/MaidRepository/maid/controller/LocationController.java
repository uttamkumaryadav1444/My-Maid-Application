package MaidRepository.maid.controller;

import MaidRepository.maid.dto.APIResponse;
import MaidRepository.maid.dto.NearbySearchDTO;
import MaidRepository.maid.dto.ORSLocationDTO;
import MaidRepository.maid.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Update user's current location
     * POST /api/location/user/update
     */
    @PostMapping("/user/update")
    public ResponseEntity<APIResponse> updateUserLocation(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            locationService.updateUserLocation(mobile, latitude, longitude);

            Map<String, Object> response = new HashMap<>();
            response.put("latitude", latitude);
            response.put("longitude", longitude);
            response.put("message", "Location updated successfully");

            return ResponseEntity.ok(APIResponse.success("Location updated", response));

        } catch (Exception e) {
            log.error("Failed to update location: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update location: " + e.getMessage()));
        }
    }

    /**
     * Update maid's current location
     * POST /api/location/maid/update
     */
    @PostMapping("/maid/update")
    public ResponseEntity<APIResponse> updateMaidLocation(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            // You'll need to get maid ID from mobile
            // This is simplified - you should get maid from repository
            // locationService.updateMaidLocation(maidId, latitude, longitude);

            Map<String, Object> response = new HashMap<>();
            response.put("latitude", latitude);
            response.put("longitude", longitude);
            response.put("message", "Location updated successfully");

            return ResponseEntity.ok(APIResponse.success("Location updated", response));

        } catch (Exception e) {
            log.error("Failed to update location: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update location: " + e.getMessage()));
        }
    }

    /**
     * Find nearby maids
     * GET /api/location/nearby?radius=10
     */
    @GetMapping("/nearby")
    public ResponseEntity<APIResponse> findNearbyMaids(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "10") Double radiusKm) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            List<NearbySearchDTO> nearbyMaids = locationService.findNearbyMaids(mobile, radiusKm);

            Map<String, Object> response = new HashMap<>();
            response.put("maids", nearbyMaids);
            response.put("count", nearbyMaids.size());
            response.put("radiusKm", radiusKm);

            return ResponseEntity.ok(APIResponse.success("Nearby maids found", response));

        } catch (Exception e) {
            log.error("Failed to find nearby maids: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to find nearby maids: " + e.getMessage()));
        }
    }

    /**
     * Calculate distance to maid
     * GET /api/location/distance-to-maid?maidId=1
     */
    @GetMapping("/distance-to-maid")
    public ResponseEntity<APIResponse> calculateDistanceToMaid(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long maidId) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            ORSLocationDTO distance = locationService.calculateUserMaidDistance(mobile, maidId);

            return ResponseEntity.ok(APIResponse.success("Distance calculated", distance));

        } catch (Exception e) {
            log.error("Failed to calculate distance: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to calculate distance: " + e.getMessage()));
        }
    }

    /**
     * Get route to maid
     * GET /api/location/route-to-maid?maidId=1
     */
    @GetMapping("/route-to-maid")
    public ResponseEntity<APIResponse> getRouteToMaid(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long maidId) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            ORSLocationDTO route = locationService.getRouteToMaid(mobile, maidId);

            return ResponseEntity.ok(APIResponse.success("Route calculated", route));

        } catch (Exception e) {
            log.error("Failed to calculate route: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to calculate route: " + e.getMessage()));
        }
    }

    /**
     * Check if maid is within service area
     * GET /api/location/in-service-area?maidId=1&maxDistance=10
     */
    @GetMapping("/in-service-area")
    public ResponseEntity<APIResponse> isMaidInServiceArea(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long maidId,
            @RequestParam(defaultValue = "10") Double maxDistanceKm) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            boolean inArea = locationService.isMaidWithinServiceArea(mobile, maidId, maxDistanceKm);

            Map<String, Object> response = new HashMap<>();
            response.put("maidId", maidId);
            response.put("inServiceArea", inArea);
            response.put("maxDistanceKm", maxDistanceKm);

            return ResponseEntity.ok(APIResponse.success("Service area check completed", response));

        } catch (Exception e) {
            log.error("Failed to check service area: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to check service area: " + e.getMessage()));
        }
    }

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