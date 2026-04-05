package MaidRepository.maid.impl;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.*;
import MaidRepository.maid.repository.BookingRepository;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.service.MaidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;  // ✅ ADD THIS IMPORT
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaidServiceImpl implements MaidService {

    @Autowired
    private MaidRepository maidRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // ✅ ADD THIS FIELD

    @Override
    public MaidResponseDTO registerMaid(MaidRequestDTO maidRequestDTO) {
        // Validate password & confirm password
        if (!maidRequestDTO.getPassword().equals(maidRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }

        // Check if mobile already exists
        if (maidRepository.existsByMobile(maidRequestDTO.getMobile())) {
            throw new IllegalArgumentException("Mobile number already registered");
        }

        // Create new maid
        Maid maid = new Maid();
        maid.setName(maidRequestDTO.getFullName());
        maid.setGender(maidRequestDTO.getGender());
        maid.setDob(maidRequestDTO.getDob());
        maid.setMobile(maidRequestDTO.getMobile());
        maid.setEmail(maidRequestDTO.getEmail());
        maid.setPassword(passwordEncoder.encode(maidRequestDTO.getPassword()));  // ✅ NOW WORKS
        maid.setIsAvailable(true);
        maid.setStatus(Maid.MaidStatus.PENDING);
        maid.setIsVerified(false);

        // Set default values for other fields to avoid null issues
        maid.setServiceType(ServiceType.GENERAL);
        maid.setHourlyRate(0.0);
        maid.setSkills("");
        maid.setCity("");
        maid.setLocality("");
        maid.setPincode("");

        Maid savedMaid = maidRepository.save(maid);
        return convertToMaidResponseDTO(savedMaid);
    }

    @Override
    public MaidResponseDTO getMaidById(Long id) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        return convertToMaidResponseDTO(maid);
    }

    @Override
    public MaidResponseDTO getMaidByMobile(String mobile) {
        Maid maid = maidRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        return convertToMaidResponseDTO(maid);
    }

    @Override
    public MaidResponseDTO getMaidByEmail(String email) {
        Maid maid = maidRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        return convertToMaidResponseDTO(maid);
    }

    @Override
    public MaidResponseDTO updateMaid(Long id, MaidUpdateRequestDTO maidUpdateDTO) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        if (maidUpdateDTO.getName() != null) maid.setName(maidUpdateDTO.getName());
        if (maidUpdateDTO.getGender() != null) maid.setGender(maidUpdateDTO.getGender());
        if (maidUpdateDTO.getDob() != null) maid.setDob(maidUpdateDTO.getDob());
        if (maidUpdateDTO.getEmail() != null) maid.setEmail(maidUpdateDTO.getEmail());
        if (maidUpdateDTO.getCity() != null) maid.setCity(maidUpdateDTO.getCity());
        if (maidUpdateDTO.getLocality() != null) maid.setLocality(maidUpdateDTO.getLocality());
        if (maidUpdateDTO.getPincode() != null) maid.setPincode(maidUpdateDTO.getPincode());
        if (maidUpdateDTO.getServiceType() != null) maid.setServiceType(maidUpdateDTO.getServiceType());
        if (maidUpdateDTO.getSkills() != null) maid.setSkills(maidUpdateDTO.getSkills());
        if (maidUpdateDTO.getExperience() != null) maid.setExperience(maidUpdateDTO.getExperience());
        if (maidUpdateDTO.getHourlyRate() != null) maid.setHourlyRate(maidUpdateDTO.getHourlyRate());
        if (maidUpdateDTO.getStatus() != null) maid.setStatus(maidUpdateDTO.getStatus());
        if (maidUpdateDTO.getIsAvailable() != null) maid.setIsAvailable(maidUpdateDTO.getIsAvailable());
        Maid updatedMaid = maidRepository.save(maid);
        return convertToMaidResponseDTO(updatedMaid);
    }

    @Override
    public void deleteMaid(Long id) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        maidRepository.delete(maid);
    }

    @Override
    public MaidResponseDTO updateMaidAvailability(Long id, Boolean available) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        maid.setIsAvailable(available);
        Maid updatedMaid = maidRepository.save(maid);
        return convertToMaidResponseDTO(updatedMaid);
    }

    @Override
    public MaidResponseDTO updateMaidHourlyRate(Long id, Double hourlyRate) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        maid.setHourlyRate(hourlyRate);
        Maid updatedMaid = maidRepository.save(maid);
        return convertToMaidResponseDTO(updatedMaid);
    }

    @Override
    public MaidResponseDTO updateMaidStatus(Long id, String status) {
        Maid maid = maidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        try {
            Maid.MaidStatus maidStatus = Maid.MaidStatus.valueOf(status.toUpperCase());
            maid.setStatus(maidStatus);
            Maid updatedMaid = maidRepository.save(maid);
            return convertToMaidResponseDTO(updatedMaid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @Override
    public List<MaidResponseDTO> getAllMaids() {
        return maidRepository.findAll().stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MaidResponseDTO> searchMaids(MaidSearchRequestDTO searchRequest) {
        Pageable pageable = PageRequest.of(
                searchRequest.getPage() != null ? searchRequest.getPage() : 0,
                searchRequest.getSize() != null ? searchRequest.getSize() : 10,
                Sort.by(Sort.Direction.DESC, "id")
        );
        return maidRepository.findAll(pageable)
                .map(this::convertToMaidResponseDTO);
    }

    @Override
    public List<MaidResponseDTO> getMaidsByServiceType(String serviceType) {
        try {
            ServiceType type = ServiceType.valueOf(serviceType.toUpperCase());
            return maidRepository.findByServiceType(type).stream()
                    .map(this::convertToMaidResponseDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type");
        }
    }

    @Override
    public List<MaidResponseDTO> getMaidsByCity(String city) {
        return maidRepository.findByCity(city).stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getMaidsByCityAndServiceType(String city, String serviceType) {
        try {
            ServiceType type = ServiceType.valueOf(serviceType.toUpperCase());
            return maidRepository.findByCityAndServiceType(city, type).stream()
                    .map(this::convertToMaidResponseDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type");
        }
    }

    @Override
    public List<MaidResponseDTO> searchMaidsByName(String name) {
        return maidRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getNearbyMaids(Double latitude, Double longitude, Double radius) {
        return maidRepository.findAll().stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getMaidsBySkill(String skill) {
        return maidRepository.findBySkillsContainingIgnoreCase(skill).stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getActiveMaids() {
        return maidRepository.findByIsAvailableTrue().stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaidResponseDTO updateProfilePhoto(Long maidId, String photoUrl) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        maid.setProfilePhotoUrl(photoUrl);
        Maid updatedMaid = maidRepository.save(maid);
        return convertToMaidResponseDTO(updatedMaid);
    }

    @Override
    public MaidResponseDTO addDocument(Long maidId, String documentUrl, String documentType) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        String currentDocs = maid.getDocumentUrls();
        if (currentDocs == null) {
            maid.setDocumentUrls(documentUrl);
        } else {
            maid.setDocumentUrls(currentDocs + "," + documentUrl);
        }
        Maid updatedMaid = maidRepository.save(maid);
        return convertToMaidResponseDTO(updatedMaid);
    }

    @Override
    public MaidResponseDTO deleteProfilePhoto(Long maidId) throws IOException {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        maid.setProfilePhotoUrl(null);
        Maid updatedMaid = maidRepository.save(maid);
        return convertToMaidResponseDTO(updatedMaid);
    }

    @Override
    public MaidResponseDTO removeDocument(Long maidId, String documentUrl) throws IOException {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
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
        Maid updatedMaid = maidRepository.save(maid);
        return convertToMaidResponseDTO(updatedMaid);
    }

    @Override
    public List<MaidDocumentDTO> getMaidDocuments(Long maidId) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        return java.util.Collections.emptyList();
    }

    @Override
    public MaidResponseDTO updateExperienceAndSkills(Long maidId, Integer experience, String skills) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));
        if (experience != null) maid.setExperience(experience);
        if (skills != null) maid.setSkills(skills);
        Maid updatedMaid = maidRepository.save(maid);
        return convertToMaidResponseDTO(updatedMaid);
    }

    @Override
    public List<MaidResponseDTO> getVerifiedMaids() {
        return maidRepository.findByIsVerifiedTrue().stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getMaidsByMinExperience(Integer minExperience) {
        return maidRepository.findByExperienceGreaterThanEqual(minExperience).stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> getMaidsByHourlyRateRange(Double minRate, Double maxRate) {
        return maidRepository.findByHourlyRateBetween(minRate, maxRate).stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> advancedSearch(String city, String serviceType, Integer minExperience,
                                                Double maxHourlyRate, Boolean isAvailable) {
        List<Maid> maids = maidRepository.findAll();
        return maids.stream()
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
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> sortMaids(String sortBy, String direction) {
        List<Maid> maids = maidRepository.findAll();
        Comparator<Maid> comparator = switch (sortBy.toLowerCase()) {
            case "hourlyrate" -> Comparator.comparing(Maid::getHourlyRate,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "experience" -> Comparator.comparing(Maid::getExperience,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "rating" -> Comparator.comparing(Maid::getAverageRating,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "name" -> Comparator.comparing(Maid::getName,
                    Comparator.nullsLast(String::compareToIgnoreCase));
            default -> Comparator.comparing(Maid::getId);
        };
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        return maids.stream()
                .sorted(comparator)
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MaidResponseDTO> getPaginatedMaids(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Maid> maidPage = maidRepository.findAll(pageable);
        return maidPage.map(this::convertToMaidResponseDTO);
    }

    // ✅ NEW METHOD: Find available maids for a specific service date and time
    // (Not in interface, so no @Override)
    public List<MaidResponseDTO> findAvailableMaidsForService(MaidSearchRequestDTO request) {
        // 1. Validate input
        if (request.getServiceType() == null || request.getServiceType().trim().isEmpty()) {
            throw new IllegalArgumentException("Service type is required");
        }
        if (request.getServiceDate() == null) {
            throw new IllegalArgumentException("Service date is required");
        }
        if (request.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is required");
        }
        if (request.getDurationHours() == null || request.getDurationHours() < 1) {
            throw new IllegalArgumentException("Duration hours must be at least 1");
        }

        // 2. Parse service type (assuming enum)
        ServiceType serviceType;
        try {
            serviceType = ServiceType.valueOf(request.getServiceType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type: " + request.getServiceType());
        }

        // 3. Get all maids with matching service type and availability flag
        List<Maid> maidsByType = maidRepository.findByServiceTypeAndIsAvailableTrue(serviceType.name());
        // because serviceType is an enum, convert to String using .name()
        // 4. Calculate end time
        LocalTime endTime = request.getStartTime().plusHours(request.getDurationHours());

        // 5. Filter maids that have no conflicting bookings
        List<Maid> availableMaids = maidsByType.stream()
                .filter(maid -> !hasConflictingBooking(maid.getId(), request.getServiceDate(),
                        request.getStartTime(), endTime))
                .collect(Collectors.toList());

        // 6. Convert to DTO and return
        return availableMaids.stream()
                .map(this::convertToMaidResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper method to check for conflicting bookings
    private boolean hasConflictingBooking(Long maidId, LocalDate date, LocalTime start, LocalTime end) {
        List<Booking> conflicts = bookingRepository.findConflictingBookings(maidId, date, start, end);
        return !conflicts.isEmpty();
    }

    // ✅ FIXED: Convert enum to String
    private MaidResponseDTO convertToMaidResponseDTO(Maid maid) {
        MaidResponseDTO dto = new MaidResponseDTO();
        dto.setId(maid.getId());
        dto.setName(maid.getName());
        dto.setGender(maid.getGender());
        dto.setDob(maid.getDob());
        dto.setMobile(maid.getMobile());
        dto.setEmail(maid.getEmail());
        // ✅ FIX: Convert enum to String using .name()
        dto.setServiceType(maid.getServiceType() != null ? maid.getServiceType(): null);
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
        dto.setCreatedAt(maid.getCreatedAt());
        dto.setUpdatedAt(maid.getUpdatedAt());
        return dto;
    }
}