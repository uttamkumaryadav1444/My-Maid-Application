// Update ORSService.java - Add missing import
package MaidRepository.maid.service;

import MaidRepository.maid.dto.ORSLocationDTO;
import MaidRepository.maid.dto.ORSRouteResponse;
import MaidRepository.maid.model.Maid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ORSService {

    @Value("${ors.api.key}")
    private String apiKey;

    @Value("${ors.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calculate distance between two locations
     */
    public ORSLocationDTO calculateDistance(
            double sourceLat, double sourceLng,
            double destLat, double destLng) {

        try {
            String url = baseUrl + "/v2/directions/driving-car";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("coordinates", List.of(
                    List.of(sourceLng, sourceLat),
                    List.of(destLng, destLat)
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<ORSRouteResponse> response = restTemplate.postForEntity(
                    url, entity, ORSRouteResponse.class);

            if (response.getBody() != null && !response.getBody().getRoutes().isEmpty()) {
                ORSRouteResponse.Route route = response.getBody().getRoutes().get(0);
                ORSRouteResponse.Summary summary = route.getSummary();

                ORSLocationDTO result = new ORSLocationDTO();
                result.setDistanceKm(summary.getDistance() / 1000.0);
                result.setDurationMinutes(summary.getDuration() / 60.0);
                result.setSourceLat(sourceLat);
                result.setSourceLng(sourceLng);
                result.setDestLat(destLat);
                result.setDestLng(destLng);

                return result;
            }

            throw new RuntimeException("No route found");

        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate distance: " + e.getMessage());
        }
    }
}