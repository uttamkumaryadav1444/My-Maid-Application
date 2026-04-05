package MaidRepository.maid.controller;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.Address;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.AddressRepository;
import MaidRepository.maid.repository.UserRepository;
import MaidRepository.maid.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/address")
public class AddressController {

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
     *  ADD NEW ADDRESS (Current/Permanent)
     */
    @PostMapping("/add")
    public ResponseEntity<APIResponse> addAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddressRequestDTO request) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            AddressResponseDTO savedAddress = addressService.addAddress(user, request);

            return ResponseEntity.ok(APIResponse.success("Address added successfully", savedAddress));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to add address: " + e.getMessage()));
        }
    }

    /**
     *  GET ALL USER ADDRESSES
     */
    @GetMapping("/all")
    public ResponseEntity<APIResponse> getUserAddresses(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<AddressResponseDTO> addresses = addressService.getUserAddresses(user);

            return ResponseEntity.ok(APIResponse.success("Addresses fetched", addresses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch addresses: " + e.getMessage()));
        }
    }

    /**
     * GET DEFAULT/CURRENT ADDRESS
     */
    @GetMapping("/current")
    public ResponseEntity<APIResponse> getCurrentAddress(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Address currentAddress = addressRepository.findByUserAndIsCurrentTrue(user)
                    .orElse(null);

            if (currentAddress == null) {
                return ResponseEntity.ok(APIResponse.success("No current address set", null));
            }

            AddressResponseDTO dto = convertToDTO(currentAddress);
            return ResponseEntity.ok(APIResponse.success("Current address", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to fetch current address: " + e.getMessage()));
        }
    }

    /**
     *  SET CURRENT ADDRESS FOR SERVICE
     * (User jahan service chahata hai)
     */
    @PostMapping("/set-current")
    public ResponseEntity<APIResponse> setCurrentAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ServiceLocationRequestDTO request) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            AddressResponseDTO currentLocation;

            if (request.getUseGpsLocation()) {
                // GPS se aayi location ko temporary address banake save karo
                currentLocation = addressService.saveGpsLocation(user, request);
            } else {
                // Saved address select kiya
                currentLocation = addressService.setAsCurrentAddress(user, request.getAddressId());
            }

            // User ki current location update karo
            user.setCurrentCity(currentLocation.getCity());
            user.setCurrentArea(currentLocation.getArea());
            user.setCurrentLatitude(currentLocation.getLatitude());
            user.setCurrentLongitude(currentLocation.getLongitude());
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("address", currentLocation);
            response.put("message", "Current location set for service");

            return ResponseEntity.ok(APIResponse.success("Location set successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to set location: " + e.getMessage()));
        }
    }

    /**
     * UPDATE ADDRESS
     */
    @PutMapping("/update/{addressId}")
    public ResponseEntity<APIResponse> updateAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long addressId,
            @RequestBody AddressRequestDTO request) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            AddressResponseDTO updatedAddress = addressService.updateAddress(user, addressId, request);

            return ResponseEntity.ok(APIResponse.success("Address updated", updatedAddress));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to update address: " + e.getMessage()));
        }
    }

    /**
     *  DELETE ADDRESS
     */
    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<APIResponse> deleteAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long addressId) {
        try {
            String mobile = extractMobileFromAuthHeader(authHeader);
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            addressService.deleteAddress(user, addressId);

            return ResponseEntity.ok(APIResponse.success("Address deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("Failed to delete address: " + e.getMessage()));
        }
    }

    // Helper methods
    private String extractMobileFromAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) return null;
        String header = authHeader.trim();
        if (header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        } else if (header.startsWith("Mobile ")) {
            return header.substring(7).trim();
        } else {
            return header;
        }
    }

    private AddressResponseDTO convertToDTO(Address address) {
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