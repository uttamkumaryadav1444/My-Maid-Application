package MaidRepository.maid.service;

import MaidRepository.maid.dto.*;
import org.springframework.data.domain.Page;
import java.io.IOException;
import java.util.List;

public interface MaidService {
    // Registration & Basic CRUD
    MaidResponseDTO registerMaid(MaidRequestDTO maidRequestDTO);
    MaidResponseDTO getMaidById(Long id);
    MaidResponseDTO updateMaid(Long id, MaidUpdateRequestDTO maidUpdateDTO);
    void deleteMaid(Long id);
    MaidResponseDTO getMaidByMobile(String mobile);
    MaidResponseDTO getMaidByEmail(String email);

    // Search & Filter Methods
    List<MaidResponseDTO> getAllMaids();
    Page<MaidResponseDTO> searchMaids(MaidSearchRequestDTO searchRequest);
    List<MaidResponseDTO> getMaidsByServiceType(String serviceType);
    List<MaidResponseDTO> getMaidsByCity(String city);
    List<MaidResponseDTO> getMaidsByCityAndServiceType(String city, String serviceType);
    List<MaidResponseDTO> searchMaidsByName(String name);
    List<MaidResponseDTO> getNearbyMaids(Double latitude, Double longitude, Double radius);
    List<MaidResponseDTO> getMaidsBySkill(String skill);
    List<MaidResponseDTO> getActiveMaids();
    List<MaidResponseDTO> getVerifiedMaids();
    List<MaidResponseDTO> getMaidsByMinExperience(Integer minExperience);
    List<MaidResponseDTO> getMaidsByHourlyRateRange(Double minRate, Double maxRate);
    List<MaidResponseDTO> advancedSearch(String city, String serviceType, Integer minExperience,
                                         Double maxHourlyRate, Boolean isAvailable);
    List<MaidResponseDTO> sortMaids(String sortBy, String direction);

    Page<MaidResponseDTO> getPaginatedMaids(int page, int size, String sortBy, String direction);

    // Status & Profile Updates
    MaidResponseDTO updateMaidAvailability(Long id, Boolean available);
    MaidResponseDTO updateMaidHourlyRate(Long id, Double hourlyRate);
    MaidResponseDTO updateMaidStatus(Long id, String status);
    MaidResponseDTO updateExperienceAndSkills(Long maidId, Integer experience, String skills);

    // Document & Photo Management
    MaidResponseDTO updateProfilePhoto(Long maidId, String photoUrl);
    MaidResponseDTO addDocument(Long maidId, String documentUrl, String documentType);
    MaidResponseDTO deleteProfilePhoto(Long maidId) throws IOException;
    MaidResponseDTO removeDocument(Long maidId, String documentUrl) throws IOException;
    List<MaidDocumentDTO> getMaidDocuments(Long maidId);
}