package MaidRepository.maid.impl;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.*;
import MaidRepository.maid.model.Booking.BookingStatus;
import MaidRepository.maid.repository.*;
import MaidRepository.maid.service.FileStorageService;
import MaidRepository.maid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaidRepository maidRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RatingReviewRepository ratingReviewRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // ================= USER METHODS =================

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        throw new UnsupportedOperationException("Use AuthService.registerUser instead");
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        return convertToUserResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) {
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already registered");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getProfilePhotoUrl() != null) user.setProfilePhotoUrl(request.getProfilePhotoUrl());
        if (request.getLanguages() != null) user.setLanguages(request.getLanguages());

        return convertToUserResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        userRepository.delete(user);
    }

    @Override
    public UserResponseDTO updateProfilePhoto(Long userId, String photoUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setProfilePhotoUrl(photoUrl);
        return convertToUserResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO deleteProfilePhoto(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setProfilePhotoUrl(null);
        return convertToUserResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO getUserByMobile(String mobile) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found with mobile: " + mobile));
        return convertToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return convertToUserResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> searchUsersByName(String name) {
        return userRepository.findAll().stream()
                .filter(u -> u.getFullName() != null &&
                        u.getFullName().toLowerCase().contains(name.toLowerCase()))
                .map(this::convertToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getVerifiedUsers() {
        return userRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsVerified()))
                .map(this::convertToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getUsersByCity(String city) {
        return getAllUsers();
    }

    @Override
    public UserResponseDTO updateUserLanguages(String mobile, Set<Language> languages) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setLanguages(languages);
        return convertToUserResponseDTO(userRepository.save(user));
    }

    @Override
    public boolean isUserSubscribed(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return Boolean.TRUE.equals(user.getIsSubscribed());
    }

    @Override
    public boolean isUserSubscribed(String mobile) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return Boolean.TRUE.equals(user.getIsSubscribed());
    }

    // ================= MAID METHODS =================

    @Override
    public List<MaidResponseDTO> getAllAvailableMaids(String mobile) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String userCity = user.getCity();
        if (userCity == null || userCity.isEmpty()) {
            throw new IllegalArgumentException("Please set your city in profile");
        }

        return maidRepository.findByIsAvailableTrue().stream()
                .filter(maid -> maid.getCity() != null && maid.getCity().equalsIgnoreCase(userCity))
                .map(this::convertToMaidDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaidResponseDTO> searchMaids(MaidSearchRequestDTO request, String mobile) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String userCity = user.getCity();
        if (userCity == null || userCity.isEmpty()) {
            throw new IllegalArgumentException("Please set your city in profile before searching");
        }

        // ✅ FIX: Convert String to ServiceType enum
        ServiceType serviceType;
        try {
            serviceType = ServiceType.valueOf(request.getServiceType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type: " + request.getServiceType());
        }

        List<Maid> maidsByType = maidRepository.findByServiceTypeAndIsAvailableTrue(serviceType);

        List<Maid> maidsInCity = maidsByType.stream()
                .filter(maid -> maid.getCity() != null && maid.getCity().equalsIgnoreCase(userCity))
                .collect(Collectors.toList());

        return maidsInCity.stream()
                .map(this::convertToMaidDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUserByMobile(String mobile, UserUpdateRequestDTO request) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) {
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already registered");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getProfilePhotoUrl() != null) user.setProfilePhotoUrl(request.getProfilePhotoUrl());
        if (request.getLanguages() != null) user.setLanguages(request.getLanguages());

        return convertToUserResponseDTO(userRepository.save(user));
    }

    @Override
    public Map<String, Object> searchMaidsWithCriteria(User user, MaidSearchCriteriaDTO criteria) {
        return new HashMap<>(); // Simplified
    }

    // ================= DTO CONVERTERS =================

    private UserResponseDTO convertToUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setMobile(user.getMobile());
        dto.setEmail(user.getEmail());
        dto.setCity(user.getCity());
        dto.setProfilePhotoUrl(user.getProfilePhotoUrl());
        dto.setIsVerified(user.getIsVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setIsSubscribed(user.getIsSubscribed());
        dto.setLanguages(user.getLanguages());
        return dto;
    }

    private MaidResponseDTO convertToMaidDTO(Maid maid) {
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
}