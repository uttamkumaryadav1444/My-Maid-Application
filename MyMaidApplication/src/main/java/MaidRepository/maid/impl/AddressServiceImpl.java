// AddressServiceImpl.java
package MaidRepository.maid.impl;

import MaidRepository.maid.dto.AddressRequestDTO;
import MaidRepository.maid.dto.AddressResponseDTO;
import MaidRepository.maid.dto.ServiceLocationRequestDTO;
import MaidRepository.maid.model.Address;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.AddressRepository;
import MaidRepository.maid.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public AddressResponseDTO addAddress(User user, AddressRequestDTO request) {
        // Validate - only one current address
        if (request.getIsCurrent() != null && request.getIsCurrent()) {
            addressRepository.findByUserAndIsCurrentTrue(user)
                    .ifPresent(oldCurrent -> {
                        oldCurrent.setIsCurrent(false);
                        addressRepository.save(oldCurrent);
                    });
        }

        // Validate - only one default address
        if (request.getIsDefault() != null && request.getIsDefault()) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .ifPresent(oldDefault -> {
                        oldDefault.setIsDefault(false);
                        addressRepository.save(oldDefault);
                    });
        }

        Address address = new Address();
        address.setUser(user);
        address.setAddressType(request.getAddressType());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setCountry("India");
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setIsGpsFetched(request.getIsGpsFetched() != null ? request.getIsGpsFetched() : false);
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        address.setIsCurrent(request.getIsCurrent() != null ? request.getIsCurrent() : false);

        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Override
    public AddressResponseDTO updateAddress(User user, Long addressId, AddressRequestDTO request) {
        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // Handle current address change
        if (request.getIsCurrent() != null && request.getIsCurrent() && !address.getIsCurrent()) {
            addressRepository.findByUserAndIsCurrentTrue(user)
                    .ifPresent(oldCurrent -> {
                        oldCurrent.setIsCurrent(false);
                        addressRepository.save(oldCurrent);
                    });
            address.setIsCurrent(true);
        } else if (request.getIsCurrent() != null && !request.getIsCurrent()) {
            address.setIsCurrent(false);
        }

        // Handle default address change
        if (request.getIsDefault() != null && request.getIsDefault() && !address.getIsDefault()) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .ifPresent(oldDefault -> {
                        oldDefault.setIsDefault(false);
                        addressRepository.save(oldDefault);
                    });
            address.setIsDefault(true);
        } else if (request.getIsDefault() != null && !request.getIsDefault()) {
            address.setIsDefault(false);
        }

        // Update fields
        if (request.getAddressLine1() != null) address.setAddressLine1(request.getAddressLine1());
        if (request.getAddressLine2() != null) address.setAddressLine2(request.getAddressLine2());
        if (request.getLandmark() != null) address.setLandmark(request.getLandmark());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getState() != null) address.setState(request.getState());
        if (request.getPincode() != null) address.setPincode(request.getPincode());
        if (request.getLatitude() != null) address.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) address.setLongitude(request.getLongitude());
        if (request.getIsGpsFetched() != null) address.setIsGpsFetched(request.getIsGpsFetched());

        Address updatedAddress = addressRepository.save(address);
        return convertToDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(User user, Long addressId) {
        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // If deleting current address, remove current flag from user too
        if (address.getIsCurrent()) {
            // Optionally set another address as current
        }

        addressRepository.delete(address);
    }

    @Override
    public List<AddressResponseDTO> getUserAddresses(User user) {
        return addressRepository.findByUserOrderByIsCurrentDescCreatedAtDesc(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponseDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
        return convertToDTO(address);
    }

    @Override
    public AddressResponseDTO setAsCurrentAddress(User user, Long addressId) {
        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // Remove current flag from old current address
        addressRepository.findByUserAndIsCurrentTrue(user)
                .ifPresent(oldCurrent -> {
                    oldCurrent.setIsCurrent(false);
                    addressRepository.save(oldCurrent);
                });

        // Set new current address
        address.setIsCurrent(true);
        Address updatedAddress = addressRepository.save(address);

        return convertToDTO(updatedAddress);
    }

    @Override
    public AddressResponseDTO saveGpsLocation(User user, ServiceLocationRequestDTO request) {
        // First, check if we already have a GPS address for today
        // For simplicity, create new one

        Address gpsAddress = new Address();
        gpsAddress.setUser(user);
        gpsAddress.setAddressType("GPS_CURRENT");
        gpsAddress.setAddressLine1(request.getFullAddress() != null ? request.getFullAddress() : "GPS Location");
        gpsAddress.setCity(request.getCity());
        gpsAddress.setLatitude(request.getLatitude());
        gpsAddress.setLongitude(request.getLongitude());
        gpsAddress.setIsGpsFetched(true);
        gpsAddress.setIsCurrent(true);
        gpsAddress.setState(""); // Will be filled by reverse geocoding if needed
        gpsAddress.setPincode("");

        // Remove current flag from any existing current address
        addressRepository.findByUserAndIsCurrentTrue(user)
                .ifPresent(oldCurrent -> {
                    oldCurrent.setIsCurrent(false);
                    addressRepository.save(oldCurrent);
                });

        Address savedAddress = addressRepository.save(gpsAddress);
        return convertToDTO(savedAddress);
    }

    @Override
    public Address getCurrentAddressEntity(User user) {
        return addressRepository.findByUserAndIsCurrentTrue(user)
                .orElseThrow(() -> new IllegalArgumentException("No current address set"));
    }

    @Override
    public AddressResponseDTO getCurrentAddress(User user) {
        Address address = addressRepository.findByUserAndIsCurrentTrue(user).orElse(null);
        return address != null ? convertToDTO(address) : null;
    }

    @Override
    public boolean hasCurrentAddress(User user) {
        return addressRepository.existsByUserAndIsCurrentTrue(user);
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
        dto.setCountry(address.getCountry());
        dto.setLatitude(address.getLatitude());
        dto.setLongitude(address.getLongitude());
        dto.setIsGpsFetched(address.getIsGpsFetched());
        dto.setIsDefault(address.getIsDefault());
        dto.setIsCurrent(address.getIsCurrent());
        return dto;
    }
}