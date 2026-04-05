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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private RatingReviewRepository ratingReviewRepository;  // needed for total reviews count

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

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null) {
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already registered");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getProfilePhotoUrl() != null) {
            user.setProfilePhotoUrl(request.getProfilePhotoUrl());
        }

        return convertToUserResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (user.getProfilePhotoUrl() != null) {
            try {
                fileStorageService.deleteFile(user.getProfilePhotoUrl());
            } catch (Exception ignored) {}
        }

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

        if (user.getProfilePhotoUrl() == null) {
            throw new IllegalArgumentException("No profile photo to delete");
        }

        fileStorageService.deleteFile(user.getProfilePhotoUrl());
        user.setProfilePhotoUrl(null);

        return convertToUserResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO getUserByMobile(String mobile) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return convertToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
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
        return getAllUsers(); // Simplified – adjust if needed
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

        String serviceType = request.getServiceType().toUpperCase();
        List<Maid> maidsByType = maidRepository.findByServiceTypeAndIsAvailableTrue(serviceType);

        List<Maid> maidsInCity = maidsByType.stream()
                .filter(maid -> maid.getCity() != null && maid.getCity().equalsIgnoreCase(userCity))
                .collect(Collectors.toList());

        LocalTime requestedEnd = request.getStartTime().plusHours(request.getDurationHours());

        List<Maid> availableMaids = new ArrayList<>();
        for (Maid maid : maidsInCity) {
            List<Booking> bookings = bookingRepository.findByMaidAndServiceDateAndStatusIn(
                    maid,
                    request.getServiceDate(),
                    List.of(BookingStatus.ACCEPTED, BookingStatus.PENDING)
            );

            boolean isAvailable = true;
            for (Booking booking : bookings) {
                LocalTime existingStart = booking.getStartTime();
                LocalTime existingEnd = existingStart.plusHours(booking.getDurationHours());

                if (request.getStartTime().isBefore(existingEnd)
                        && requestedEnd.isAfter(existingStart)) {
                    isAvailable = false;
                    break;
                }
            }

            if (isAvailable) {
                availableMaids.add(maid);
            }
        }

        return availableMaids.stream()
                .map(this::convertToMaidDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUserByMobile(String mobile, UserUpdateRequestDTO request) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already registered");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getProfilePhotoUrl() != null) {
            user.setProfilePhotoUrl(request.getProfilePhotoUrl());
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponseDTO(updatedUser);
    }

    // ================= NEW ADVANCED SEARCH =================

    @Override
    public Map<String, Object> searchMaidsWithCriteria(User user, MaidSearchCriteriaDTO criteria) {
        // 1. Determine search city
        String searchCity = criteria.getCity();
        if (searchCity == null || searchCity.trim().isEmpty()) {
            searchCity = user.getCity();
            if (searchCity == null || searchCity.isEmpty()) {
                throw new IllegalArgumentException("Please set your city in profile or provide city in search");
            }
        }

        // 2. Validate and parse service type
        if (criteria.getServiceType() == null || criteria.getServiceType().trim().isEmpty()) {
            throw new IllegalArgumentException("Service type is required");
        }
        ServiceType serviceType;
        try {
            serviceType = ServiceType.valueOf(criteria.getServiceType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type: " + criteria.getServiceType());
        }

        // 3. Fetch maids by service type and city (only those with isAvailable = true)
        List<Maid> maids = maidRepository.findAvailableByServiceTypeAndCity(serviceType, searchCity);

        // 4. If date/time provided, filter by availability (no conflicting bookings)
        LocalDate searchDate = criteria.getDate();
        LocalTime searchTime = criteria.getTime();
        Integer duration = criteria.getDurationHours();

        List<Maid> availableMaids = new ArrayList<>();
        for (Maid maid : maids) {
            boolean isAvailableForSlot = true;
            if (searchDate != null && searchTime != null && duration != null) {
                LocalTime endTime = searchTime.plusHours(duration);
                List<Booking> conflicts = bookingRepository.findConflictingBookings(
                        maid.getId(), searchDate, searchTime, endTime);
                if (!conflicts.isEmpty()) {
                    isAvailableForSlot = false;
                }
            }
            if (isAvailableForSlot) {
                availableMaids.add(maid);
            }
        }

        // 5. Apply availability window filter (NOW, TODAY, TOMORROW, WEEK, MONTH)
        if (criteria.getAvailability() != null && !criteria.getAvailability().isEmpty()) {
            // For simplicity, we rely on the maid's isAvailable flag and the slot check above.
            // You can extend this with more sophisticated logic.
        }

        // 6. Convert to DTOs and compute extra fields
        List<MaidSearchResponseDTO> dtos = new ArrayList<>();
        for (Maid maid : availableMaids) {
            MaidSearchResponseDTO dto = new MaidSearchResponseDTO();
            dto.setId(maid.getId());
            dto.setName(maid.getName());
            dto.setProfilePhotoUrl(maid.getProfilePhotoUrl());
            dto.setServiceType(maid.getServiceType());
            dto.setAverageRating(maid.getAverageRating() != null ? maid.getAverageRating() : 0.0);
            dto.setTotalReviews(ratingReviewRepository.countByMaidId(maid.getId()).intValue());
            dto.setExperience(maid.getExperience());
            dto.setHourlyRate(maid.getHourlyRate());
            dto.setCity(maid.getCity());
            dto.setLocality(maid.getLocality());
            dto.setIsAvailable(maid.getIsAvailable());

            // Compute availability status (Now, Today, Tomorrow)
            dto.setAvailabilityStatus(computeAvailabilityStatus(maid, criteria));

            // Compute distance if user location provided
            if (criteria.getLatitude() != null && criteria.getLongitude() != null &&
                    maid.getLatitude() != null && maid.getLongitude() != null) {
                double distance = calculateDistance(criteria.getLatitude(), criteria.getLongitude(),
                        maid.getLatitude(), maid.getLongitude());
                dto.setDistanceKm(distance);
            } else {
                dto.setDistanceKm(null);
            }

            dtos.add(dto);
        }

        // 7. Apply sorting based on filter
        if (criteria.getFilter() != null) {
            switch (criteria.getFilter().toUpperCase()) {
                case "TOP_RATED":
                    dtos.sort((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()));
                    break;
                case "NEARBY":
                    dtos.sort(Comparator.comparingDouble(
                            d -> d.getDistanceKm() != null ? d.getDistanceKm() : Double.MAX_VALUE));
                    break;
                case "BUDGET":
                    dtos.sort(Comparator.comparingDouble(MaidSearchResponseDTO::getHourlyRate));
                    break;
                default:
                    dtos.sort((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()));
            }
        } else {
            // default sort by rating
            dtos.sort((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()));
        }

        // 8. Pagination
        int start = Math.min(criteria.getOffset(), dtos.size());
        int end = Math.min(start + criteria.getLimit(), dtos.size());
        List<MaidSearchResponseDTO> paginated = dtos.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("total", dtos.size());
        response.put("offset", criteria.getOffset());
        response.put("limit", criteria.getLimit());
        response.put("maids", paginated);
        return response;
    }

    // Helper: compute availability status
    private String computeAvailabilityStatus(Maid maid, MaidSearchCriteriaDTO criteria) {
        if (!maid.getIsAvailable()) {
            return "Not Available";
        }
        // If user selected a specific date, show that
        if (criteria.getDate() != null) {
            return "Available on selected date";
        }
        // Otherwise, check current time against bookings (simplified)
        LocalDateTime now = LocalDateTime.now();
        // Check if maid has any booking that overlaps with now
        List<Booking> currentConflicts = bookingRepository.findConflictingBookings(
                maid.getId(), now.toLocalDate(), now.toLocalTime(), now.toLocalTime().plusHours(1));
        if (currentConflicts.isEmpty()) {
            return "Available Now";
        }
        // Check for today
        LocalDate today = now.toLocalDate();
        List<Booking> todayBookings = bookingRepository.findByMaidAndServiceDate(maid, today);
        if (todayBookings.stream().noneMatch(b -> b.getStatus() == BookingStatus.ACCEPTED)) {
            return "Available Today";
        }
        return "Available";
    }

    // Haversine distance
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ================= DTO CONVERTERS =================

    private UserResponseDTO convertToUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setMobile(user.getMobile());
        dto.setEmail(user.getEmail());
        dto.setProfilePhotoUrl(user.getProfilePhotoUrl());
        dto.setIsVerified(user.getIsVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private MaidResponseDTO convertToMaidDTO(Maid maid) {
        MaidResponseDTO dto = new MaidResponseDTO();
        dto.setId(maid.getId());
        dto.setName(maid.getName());
        // If serviceType in DTO is String, use .name() conversion; if enum, assign directly.
        dto.setServiceType(maid.getServiceType()); // assuming enum
        dto.setExperience(maid.getExperience());
        dto.setHourlyRate(maid.getHourlyRate());
        dto.setIsAvailable(maid.getIsAvailable());
        dto.setCity(maid.getCity());
        return dto;
    }
}