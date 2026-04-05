package MaidRepository.maid.service;

import MaidRepository.maid.dto.AddressRequestDTO;
import MaidRepository.maid.dto.AddressResponseDTO;
import MaidRepository.maid.dto.ServiceLocationRequestDTO;
import MaidRepository.maid.model.Address;
import MaidRepository.maid.model.User;
import java.util.List;

public interface AddressService {
    AddressResponseDTO addAddress(User user, AddressRequestDTO request);
    AddressResponseDTO updateAddress(User user, Long addressId, AddressRequestDTO request);
    void deleteAddress(User user, Long addressId);
    List<AddressResponseDTO> getUserAddresses(User user);
    AddressResponseDTO getAddressById(Long addressId);
    AddressResponseDTO setAsCurrentAddress(User user, Long addressId);
    AddressResponseDTO saveGpsLocation(User user, ServiceLocationRequestDTO request);
    Address getCurrentAddressEntity(User user);  // ← Returns Entity
    AddressResponseDTO getCurrentAddress(User user);  // ← Returns DTO
    boolean hasCurrentAddress(User user);
}