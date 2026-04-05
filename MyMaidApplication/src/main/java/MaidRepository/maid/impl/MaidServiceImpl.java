package MaidRepository.maid.impl;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.*;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.service.MaidService;
import MaidRepository.maid.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaidServiceImpl implements MaidService {

    @Autowired
    private MaidRepository maidRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    // ==================== BASIC CRUD ====================

    @Override
    public List<MaidResponseDTO> getAllMaids() {
        return maidRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaidResponseDTO getMaidById(Long id) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
        return convertToDTO(maid);
    }

    @Override
    public MaidResponseDTO updateMaid(Long id, MaidUpdateRequestDTO request) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));

        if (request.getName() != null) maid.setName(request.getName());
        if (request.getGender() != null) maid.setGender(request.getGender());
        if (request.getDob() != null) maid.setDob(request.getDob());
        if (request.getEmail() != null) maid.setEmail(request.getEmail());
        if (request.getCity() != null) maid.setCity(request.getCity());
        if (request.getLocality() != null) maid.setLocality(request.getLocality());
        if (request.getPincode() != null) maid.setPincode(request.getPincode());
        if (request.getServiceType() != null) maid.setServiceType(request.getServiceType());
        if (request.getSkills() != null) maid.setSkills(request.getSkills());
        if (request.getExperience() != null) maid.setExperience(request.getExperience());
        if (request.getHourlyRate() != null) maid.setHourlyRate(request.getHourlyRate());
        if (request.getStatus() != null) maid.setStatus(request.getStatus());
        if (request.getIsAvailable() != null) maid.setIsAvailable(request.getIsAvailable());
        if (request.getLanguages() != null) maid.setLanguages(request.getLanguages());

        return convertToDTO(maidRepository.save(maid));
    }

    @Override
    public void deleteMaid(Long id) {
        if (!maidRepository.existsById(id)) {
            throw new IllegalArgumentException("Maid not found with id: " + id);
        }
        maidRepository.deleteById(id);
    }

    @Override
    public MaidResponseDTO getMaidByMobile(String mobile) {
        Maid maid = maidRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with mobile: " + mobile));
        return convertToDTO(maid);
    }

    @Override
    public MaidResponseDTO getMaidByEmail(String email) {
        Maid maid = maidRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with email: " + email));
        return convertToDTO(maid);
    }

    // ==================== SEARCH METHODS ====================

    @Override
    public List<MaidResponseDTO> getMaidsByServiceType(String serviceType) {
        try {
            ServiceType type = ServiceType.valueOf(serviceType.toUpperCase());
            return maidRepository.findByServiceType(type).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type: " + serviceType);
        }
    }

    @Override
    public List<MaidResponseDTO> getMaidsByCity(String city) {
        return maidRepository.findByCity(city).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getMaidsByCityAndServiceType(String city, String serviceType) {
        try {
            ServiceType type = ServiceType.valueOf(serviceType.toUpperCase());
            return maidRepository.findByCityAndServiceType(city, type).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type: " + serviceType);
        }
    }

    @Override
    public List<MaidResponseDTO> searchMaidsByName(String name) {
        return maidRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getMaidsBySkill(String skill) {
        return maidRepository.findBySkillsContainingIgnoreCase(skill).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getActiveMaids() {
        return maidRepository.findByIsAvailableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getVerifiedMaids() {
        return maidRepository.findByIsVerifiedTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getMaidsByMinExperience(Integer minExperience) {
        return maidRepository.findByExperienceGreaterThanEqual(minExperience).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getMaidsByHourlyRateRange(Double minRate, Double maxRate) {
        return maidRepository.findByHourlyRateBetween(minRate, maxRate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> advancedSearch(String city, String serviceType,
                                                Integer minExperience, Double maxHourlyRate,
                                                Boolean isAvailable) {
        return maidRepository.findAll().stream()
                .filter(maid -> city == null || city.isEmpty() ||
                        (maid.getCity() != null && maid.getCity().equalsIgnoreCase(city)))
                .filter(maid -> serviceType == null || serviceType.isEmpty() ||
                        (maid.getServiceType() != null && maid.getServiceType().name().equalsIgnoreCase(serviceType)))
                .filter(maid -> minExperience == null ||
                        (maid.getExperience() != null && maid.getExperience() >= minExperience))
                .filter(maid -> maxHourlyRate == null ||
                        (maid.getHourlyRate() != null && maid.getHourlyRate() <= maxHourlyRate))
                .filter(maid -> isAvailable == null ||
                        (maid.getIsAvailable() != null && maid.getIsAvailable().equals(isAvailable)))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> sortMaids(String sortBy, String direction) {
        return maidRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MaidResponseDTO> getPaginatedMaids(int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size);
        return maidRepository.findAll(pageable).map(this::convertToDTO);
    }

    // ==================== PROFILE UPDATE METHODS ====================

    @Override
    public MaidResponseDTO updateMaidAvailability(Long id, Boolean available) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
        maid.setIsAvailable(available);
        return convertToDTO(maidRepository.save(maid));
    }

    @Override
    public MaidResponseDTO updateMaidHourlyRate(Long id, Double hourlyRate) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
        maid.setHourlyRate(hourlyRate);
        return convertToDTO(maidRepository.save(maid));
    }

    @Override
    public MaidResponseDTO updateMaidStatus(Long id, String status) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
        try {
            Maid.MaidStatus maidStatus = Maid.MaidStatus.valueOf(status.toUpperCase());
            maid.setStatus(maidStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        return convertToDTO(maidRepository.save(maid));
    }

    @Override
    public MaidResponseDTO updateExperienceAndSkills(Long id, Integer experience, String skills) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
        if (experience != null) maid.setExperience(experience);
        if (skills != null) maid.setSkills(skills);
        return convertToDTO(maidRepository.save(maid));
    }

    @Override
    public MaidResponseDTO updateProfilePhoto(Long id, String photoUrl) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
        maid.setProfilePhotoUrl(photoUrl);
        return convertToDTO(maidRepository.save(maid));
    }

    @Override
    public MaidResponseDTO addDocument(Long id, String documentUrl, String documentType) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
        String currentDocs = maid.getDocumentUrls();
        if (currentDocs == null || currentDocs.isEmpty()) {
            maid.setDocumentUrls(documentUrl);
        } else {
            maid.setDocumentUrls(currentDocs + "," + documentUrl);
        }
        maid.setDocumentTypes(documentType);
        return convertToDTO(maidRepository.save(maid));
    }

    @Override
    public MaidResponseDTO deleteProfilePhoto(Long id) throws Exception {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
        maid.setProfilePhotoUrl(null);
        return convertToDTO(maidRepository.save(maid));
    }

    @Override
    public MaidResponseDTO removeDocument(Long id, String documentUrl) throws Exception {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));
        String currentDocs = maid.getDocumentUrls();
        if (currentDocs != null && currentDocs.contains(documentUrl)) {
            String updatedDocs = currentDocs.replace(documentUrl, "")
                    .replace(",,", ",")
                    .trim();
            if (updatedDocs.endsWith(",")) {
                updatedDocs = updatedDocs.substring(0, updatedDocs.length() - 1);
            }
            if (updatedDocs.startsWith(",")) {
                updatedDocs = updatedDocs.substring(1);
            }
            maid.setDocumentUrls(updatedDocs.isEmpty() ? null : updatedDocs);
        }
        return convertToDTO(maidRepository.save(maid));
    }

    @Override
    public List<MaidDocumentDTO> getMaidDocuments(Long id) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found with id: " + id));

        String urls = maid.getDocumentUrls();
        String types = maid.getDocumentTypes();

        if (urls == null || urls.isEmpty()) {
            return List.of();
        }

        String[] urlArray = urls.split(",");
        String[] typeArray = types != null ? types.split(",") : new String[urlArray.length];

        List<MaidDocumentDTO> documents = new java.util.ArrayList<>();
        for (int i = 0; i < urlArray.length; i++) {
            MaidDocumentDTO doc = new MaidDocumentDTO();
            doc.setDocumentUrl(urlArray[i].trim());
            doc.setDocumentType(i < typeArray.length ? typeArray[i].trim() : "OTHER");
            documents.add(doc);
        }
        return documents;
    }

    // ==================== PUBLIC/PAID METHODS ====================

    @Override
    public Page<MaidPublicResponseDTO> freeSearch(FreeSearchRequestDTO request) {
        System.out.println("===== IN freeSearch SERVICE =====");
        System.out.println("Request: " + request);

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Maid> maids = maidRepository.freeSearch(
                request.getCity(),
                request.getServiceType(),
                request.getMinExperience(),
                request.getMinRating(),
                pageable
        );

        System.out.println("Repository returned: " + maids.getTotalElements() + " maids");
        System.out.println("Content: " + maids.getContent());

        return maids.map(this::convertToPublicDTO);
    }

    @Override
    public Page<MaidPrivateResponseDTO> advancedSearch(AdvancedSearchRequestDTO request, Long userId) {
        // Check subscription
        if (!subscriptionService.hasActiveSubscription(String.valueOf(userId))) {
            throw new RuntimeException("Subscription required for advanced search");
        }

        System.out.println("===== IN advancedSearch SERVICE =====");
        System.out.println("Request: " + request);
        System.out.println("UserId: " + userId);

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Maid> maids = maidRepository.advancedSearch(
                request.getCity(),
                request.getServiceType() != null ? request.getServiceType().name() : null,
                request.getMinExperience(),
                request.getMaxExperience(),
                request.getMinRating(),
                request.getMinHourlyRate(),
                request.getMaxHourlyRate(),
                request.getIsAvailable(),
                request.getLanguage() != null ? request.getLanguage().name() : null,
                request.getIsVerified(),
                request.getWorkType() != null ? request.getWorkType().name() : null,
                request.getLatitude(),
                request.getLongitude(),
                request.getRadiusKm(),
                pageable
        );

        System.out.println("Repository returned: " + maids.getTotalElements() + " maids");

        return maids.map(this::convertToPrivateDTO);
    }

    @Override
    public UnlockContactResponseDTO unlockContact(Long maidId, Long userId) {
        // Check subscription
        if (!subscriptionService.hasActiveSubscription(String.valueOf(userId))) {
            throw new RuntimeException("Active subscription required to view contact");
        }

        // Check if user has enough contact views
        if (!subscriptionService.canViewContact(String.valueOf(userId))) {
            throw new RuntimeException("No contact views remaining");
        }

        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new RuntimeException("Maid not found with id: " + maidId));

        UnlockContactResponseDTO response = new UnlockContactResponseDTO();
        response.setMaidId(maidId);
        response.setMessage("Contact unlocked successfully");
        response.setMobile(maid.getMobile());
        response.setEmail(maid.getEmail());

        // Decrement contact view count
        subscriptionService.decrementContactView(String.valueOf(userId));

        return response;
    }

    // ==================== CONVERTER METHODS ====================

    private MaidResponseDTO convertToDTO(Maid maid) {
        MaidResponseDTO dto = new MaidResponseDTO();
        dto.setId(maid.getId());
        dto.setName(maid.getName());
        dto.setGender(maid.getGender());
        dto.setDob(maid.getDob());
        dto.setMobile(maid.getMobile());
        dto.setEmail(maid.getEmail());
        dto.setServiceType(maid.getServiceType());
        dto.setSkills(maid.getSkills());
        dto.setExperience(maid.getExperience());
        dto.setCity(maid.getCity());
        dto.setLocality(maid.getLocality());
        dto.setPincode(maid.getPincode());
        dto.setLatitude(maid.getLatitude());
        dto.setLongitude(maid.getLongitude());
        dto.setProfilePhotoUrl(maid.getProfilePhotoUrl());
        dto.setStatus(maid.getStatus());
        dto.setIsVerified(maid.getIsVerified());
        dto.setIsAvailable(maid.getIsAvailable());
        dto.setAverageRating(maid.getAverageRating());
        dto.setHourlyRate(maid.getHourlyRate());
        dto.setLanguages(maid.getLanguages());
        dto.setWorkType(maid.getWorkType());
        dto.setCreatedAt(maid.getCreatedAt());
        dto.setUpdatedAt(maid.getUpdatedAt());
        return dto;
    }

    private MaidPublicResponseDTO convertToPublicDTO(Maid maid) {
        MaidPublicResponseDTO dto = new MaidPublicResponseDTO();
        dto.setId(maid.getId());
        dto.setName(maid.getName());
        dto.setServiceType(maid.getServiceType());
        dto.setExperience(maid.getExperience());
        dto.setAverageRating(maid.getAverageRating());
        dto.setCity(maid.getCity());
        dto.setLocality(maid.getLocality());
        dto.setProfilePhotoUrl(maid.getProfilePhotoUrl());
        dto.setIsAvailable(maid.getIsAvailable());
        dto.setHourlyRate(maid.getHourlyRate());
        dto.setLanguages(maid.getLanguages());
        dto.setWorkType(maid.getWorkType());
        dto.setIsVerified(maid.getIsVerified());
        // ❌ Mobile and Email are NOT set (hidden for free users)
        return dto;
    }

    private MaidPrivateResponseDTO convertToPrivateDTO(Maid maid) {
        MaidPrivateResponseDTO dto = new MaidPrivateResponseDTO();
        dto.setId(maid.getId());
        dto.setName(maid.getName());
        dto.setServiceType(maid.getServiceType());
        dto.setExperience(maid.getExperience());
        dto.setAverageRating(maid.getAverageRating());
        dto.setCity(maid.getCity());
        dto.setLocality(maid.getLocality());
        dto.setProfilePhotoUrl(maid.getProfilePhotoUrl());
        dto.setIsAvailable(maid.getIsAvailable());
        dto.setHourlyRate(maid.getHourlyRate());
        dto.setLanguages(maid.getLanguages());
        dto.setWorkType(maid.getWorkType());
        dto.setIsVerified(maid.getIsVerified());
        // ✅ Mobile and Email ARE set for paid users
        dto.setMobile(maid.getMobile());
        dto.setEmail(maid.getEmail());
        return dto;
    }
}