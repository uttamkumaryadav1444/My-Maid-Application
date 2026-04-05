package MaidRepository.maid.service;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.Language;
import MaidRepository.maid.model.Maid.WorkType;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MaidService {

    // ✅ ALL METHODS THAT YOUR CONTROLLERS ARE CALLING

    // Basic CRUD
    List<MaidResponseDTO> getAllMaids();
    MaidResponseDTO getMaidById(Long id);
    MaidResponseDTO updateMaid(Long id, MaidUpdateRequestDTO request);
    void deleteMaid(Long id);
    MaidResponseDTO getMaidByMobile(String mobile);
    MaidResponseDTO getMaidByEmail(String email);

    // Search methods
    List<MaidResponseDTO> getMaidsByServiceType(String serviceType);
    List<MaidResponseDTO> getMaidsByCity(String city);
    List<MaidResponseDTO> getMaidsByCityAndServiceType(String city, String serviceType);
    List<MaidResponseDTO> searchMaidsByName(String name);
    List<MaidResponseDTO> getMaidsBySkill(String skill);
    List<MaidResponseDTO> getActiveMaids();
    List<MaidResponseDTO> getVerifiedMaids();
    List<MaidResponseDTO> getMaidsByMinExperience(Integer minExperience);
    List<MaidResponseDTO> getMaidsByHourlyRateRange(Double minRate, Double maxRate);
    List<MaidResponseDTO> advancedSearch(String city, String serviceType, Integer minExperience,
                                         Double maxHourlyRate, Boolean isAvailable);
    List<MaidResponseDTO> sortMaids(String sortBy, String direction);
    Page<MaidResponseDTO> getPaginatedMaids(int page, int size, String sortBy, String direction);

    // Profile update methods
    MaidResponseDTO updateMaidAvailability(Long id, Boolean available);
    MaidResponseDTO updateMaidHourlyRate(Long id, Double hourlyRate);
    MaidResponseDTO updateMaidStatus(Long id, String status);
    MaidResponseDTO updateExperienceAndSkills(Long id, Integer experience, String skills);
    MaidResponseDTO updateProfilePhoto(Long id, String photoUrl);
    MaidResponseDTO addDocument(Long id, String documentUrl, String documentType);
    MaidResponseDTO deleteProfilePhoto(Long id) throws Exception;
    MaidResponseDTO removeDocument(Long id, String documentUrl) throws Exception;
    List<MaidDocumentDTO> getMaidDocuments(Long id);

    // ✅ NEW METHODS FOR PUBLIC/PAID CONTROLLERS
    Page<MaidPublicResponseDTO> freeSearch(FreeSearchRequestDTO request);
    Page<MaidPrivateResponseDTO> advancedSearch(AdvancedSearchRequestDTO request, Long userId);
    UnlockContactResponseDTO unlockContact(Long maidId, Long userId);
}