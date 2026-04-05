package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.Address;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.AddressRepository;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public AddressController(AddressService addressService,
                             UserRepository userRepository,
                             AddressRepository addressRepository) {
        this.addressService = addressService;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    /**
     * ADD NEW ADDRESS
     * POST /api/address/add
     * Headers: Authorization: Bearer token-8969161910-1772902494059
     */
    @PostMapping("/add")
    public ResponseEntity<APIResponse> addAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddressRequestDTO request) {
        try {
            log.info("Adding new address - AuthHeader: {}", authHeader);

            // Step 1: Extract mobile from header
            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                log.warn("Failed to extract mobile from auth header");
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid authorization header - could not extract mobile"));
            }
            log.info("Extracted mobile: {}", mobile);

            // Step 2: Find user by mobile
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + mobile));
            log.info("Found user: {} with ID: {}", user.getFullName(), user.getId());

            // Step 3: Add address
            AddressResponseDTO savedAddress = addressService.addAddress(user, request);
            log.info("Address added successfully with ID: {}", savedAddress.getId());

            return ResponseEntity.ok(APIResponse.success("Address added successfully", savedAddress));

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to add address: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to add address: " + e.getMessage()));
        }
    }

    /**
     * GET ALL USER ADDRESSES
     * GET /api/address/all
     */
    @GetMapping("/all")
    public ResponseEntity<APIResponse> getUserAddresses(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("Fetching all addresses");

            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid authorization header"));
            }

            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + mobile));

            List<AddressResponseDTO> addresses = addressService.getUserAddresses(user);
            log.info("Found {} addresses for user {}", addresses.size(), mobile);

            return ResponseEntity.ok(APIResponse.success("Addresses fetched successfully", addresses));

        } catch (Exception e) {
            log.error("Failed to fetch addresses: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch addresses: " + e.getMessage()));
        }
    }

    /**
     * GET CURRENT ADDRESS
     * GET /api/address/current
     */
    @GetMapping("/current")
    public ResponseEntity<APIResponse> getCurrentAddress(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("Fetching current address");

            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid authorization header"));
            }

            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + mobile));

            Address currentAddress = addressRepository.findByUserAndIsCurrentTrue(user)
                    .orElse(null);

            if (currentAddress == null) {
                return ResponseEntity.ok(APIResponse.success("No current address set", null));
            }

            AddressResponseDTO dto = convertToDTO(currentAddress);
            return ResponseEntity.ok(APIResponse.success("Current address fetched", dto));

        } catch (Exception e) {
            log.error("Failed to fetch current address: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch current address: " + e.getMessage()));
        }
    }

    /**
     * SET CURRENT ADDRESS
     * POST /api/address/set-current
     */
    @PostMapping("/set-current")
    public ResponseEntity<APIResponse> setCurrentAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ServiceLocationRequestDTO request) {
        try {
            log.info("Setting current address - UseGPS: {}", request.getUseGpsLocation());

            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid authorization header"));
            }

            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + mobile));

            AddressResponseDTO currentLocation;

            if (request.getUseGpsLocation() != null && request.getUseGpsLocation()) {
                // GPS se aayi location ko save karo
                currentLocation = addressService.saveGpsLocation(user, request);
                log.info("Saved GPS location");
            } else {
                // Saved address select kiya
                currentLocation = addressService.setAsCurrentAddress(user, request.getAddressId());
                log.info("Set address ID: {} as current", request.getAddressId());
            }

            // User ki current location update karo
            user.setCurrentCity(currentLocation.getCity());
            user.setCurrentArea(currentLocation.getArea());
            user.setCurrentLatitude(currentLocation.getLatitude());
            user.setCurrentLongitude(currentLocation.getLongitude());
            userRepository.save(user);
            log.info("Updated user's current location");

            Map<String, Object> response = new HashMap<>();
            response.put("address", currentLocation);
            response.put("message", "Current location set for service");

            return ResponseEntity.ok(APIResponse.success("Location set successfully", response));

        } catch (Exception e) {
            log.error("Failed to set location: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to set location: " + e.getMessage()));
        }
    }

    /**
     * UPDATE ADDRESS
     * PUT /api/address/update/{addressId}
     */
    @PutMapping("/update/{addressId}")
    public ResponseEntity<APIResponse> updateAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long addressId,
            @RequestBody AddressRequestDTO request) {
        try {
            log.info("Updating address ID: {}", addressId);

            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid authorization header"));
            }

            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + mobile));

            AddressResponseDTO updatedAddress = addressService.updateAddress(user, addressId, request);
            log.info("Address updated successfully");

            return ResponseEntity.ok(APIResponse.success("Address updated successfully", updatedAddress));

        } catch (Exception e) {
            log.error("Failed to update address: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update address: " + e.getMessage()));
        }
    }

    /**
     * DELETE ADDRESS
     * DELETE /api/address/delete/{addressId}
     */
    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<APIResponse> deleteAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long addressId) {
        try {
            log.info("Deleting address ID: {}", addressId);

            String mobile = extractMobileFromAuthHeader(authHeader);
            if (mobile == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.error("Invalid authorization header"));
            }

            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + mobile));

            addressService.deleteAddress(user, addressId);
            log.info("Address deleted successfully");

            return ResponseEntity.ok(APIResponse.success("Address deleted successfully", null));

        } catch (Exception e) {
            log.error("Failed to delete address: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to delete address: " + e.getMessage()));
        }
    }

    /**
     * Token se mobile number extract karne ka method
     * Supports formats:
     * - "Bearer token-8969161910-1772902494059"
     * - "Mobile 8969161910"
     * - "8969161910"
     */
    private String extractMobileFromAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            log.debug("Auth header is null or empty");
            return null;
        }

        String header = authHeader.trim();
        log.debug("Extracting mobile from header: {}", header);

        // Case 1: "Bearer token-8969161910-1772902494059"
        if (header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            log.debug("Bearer token: {}", token);

            // Token format: "token-8969161910-1772902494059"
            if (token.startsWith("token-")) {
                String[] parts = token.split("-");
                log.debug("Token parts count: {}", parts.length);

                if (parts.length >= 2) {
                    String mobile = parts[1];
                    log.debug("Extracted mobile from token: {}", mobile);
                    return mobile;
                }
            }
            log.warn("Token does not match expected format: {}", token);
            return null;
        }
        // Case 2: "Mobile 8969161910"
        else if (header.startsWith("Mobile ")) {
            String mobile = header.substring(7).trim();
            log.debug("Extracted mobile from Mobile header: {}", mobile);
            return mobile;
        }
        // Case 3: Direct mobile number
        else if (header.matches("^[6-9]\\d{9}$")) {
            log.debug("Direct mobile number: {}", header);
            return header;
        }

        log.warn("Could not extract mobile from header: {}", header);
        return null;
    }

    /**
     * Convert Address entity to DTO
     */
    private AddressResponseDTO convertToDTO(Address address) {
        if (address == null) return null;

        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(address.getId());
        dto.setAddressType(address.getAddressType());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setLandmark(address.getLandmark());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPincode(address.getPincode());
        dto.setLatitude(address.getLatitude());
        dto.setLongitude(address.getLongitude());
        dto.setIsGpsFetched(address.getIsGpsFetched());
        dto.setIsDefault(address.getIsDefault());
        dto.setIsCurrent(address.getIsCurrent());
        return dto;
    }
}