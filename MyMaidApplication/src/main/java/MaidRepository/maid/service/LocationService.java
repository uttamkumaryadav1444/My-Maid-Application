package MaidRepository.maid.service;

import MaidRepository.maid.dto.NearbySearchDTO;
import MaidRepository.maid.dto.ORSLocationDTO;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.model.User;
import MaidRepository.maid.repository.MaidRepository;
import MaidRepository.maid.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    private ORSService orsService;

    @Autowired
    private MaidRepository maidRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Calculate distance between two points
     */
    public ORSLocationDTO calculateDistance(double sourceLat, double sourceLng,
                                            double destLat, double destLng) {
        return orsService.calculateDistance(sourceLat, sourceLng, destLat, destLng);
    }

    /**
     * Update user's current location
     */
    public void updateUserLocation(String mobile, Double latitude, Double longitude) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setCurrentLatitude(latitude);
        user.setCurrentLongitude(longitude);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Update maid's last known location
     */
    public void updateMaidLocation(Long maidId, Double latitude, Double longitude) {
        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

        maid.setLastLocationLatitude(latitude);
        maid.setLastLocationLongitude(longitude);
        maid.setLastLocationUpdatedAt(LocalDateTime.now());
        maidRepository.save(maid);
    }

    /**
     * Calculate distance between user and maid
     */
    public ORSLocationDTO calculateUserMaidDistance(String userMobile, Long maidId) {
        User user = userRepository.findByMobile(userMobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Maid maid = maidRepository.findById(maidId)
                .orElseThrow(() -> new IllegalArgumentException("Maid not found"));

        if (user.getCurrentLatitude() == null || user.getCurrentLongitude() == null) {
            throw new IllegalArgumentException("User location not set");
        }

        if (maid.getLastLocationLatitude() == null || maid.getLastLocationLongitude() == null) {
            throw new IllegalArgumentException("Maid location not available");
        }

        return orsService.calculateDistance(
                user.getCurrentLatitude(), user.getCurrentLongitude(),
                maid.getLastLocationLatitude(), maid.getLastLocationLongitude()
        );
    }

    /**
     * Find nearby maids based on user location
     */
    public List<NearbySearchDTO> findNearbyMaids(String userMobile, Double radiusKm) {
        User user = userRepository.findByMobile(userMobile)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getCurrentLatitude() == null || user.getCurrentLongitude() == null) {
            throw new IllegalArgumentException("Please set your location first");
        }

        List<Maid> allMaids = maidRepository.findAll();
        List<NearbySearchDTO> nearbyMaids = new ArrayList<>();

        for (Maid maid : allMaids) {
            if (maid.getLastLocationLatitude() != null && maid.getLastLocationLongitude() != null) {
                try {
                    ORSLocationDTO distance = orsService.calculateDistance(
                            user.getCurrentLatitude(), user.getCurrentLongitude(),
                            maid.getLastLocationLatitude(), maid.getLastLocationLongitude()
                    );

                    if (distance.getDistanceKm() <= radiusKm) {
                        NearbySearchDTO nearby = new NearbySearchDTO();
                        nearby.setMaidId(maid.getId());
                        nearby.setMaidName(maid.getName());
                        nearby.setServiceType(maid.getServiceType());
                        nearby.setHourlyRate(maid.getHourlyRate());
                        nearby.setAverageRating(maid.getAverageRating());
                        nearby.setProfilePhotoUrl(maid.getProfilePhotoUrl());
                        nearby.setDistanceKm(distance.getDistanceKm());
                        nearby.setDurationMinutes(distance.getDurationMinutes());
                        nearby.setLatitude(maid.getLastLocationLatitude());
                        nearby.setLongitude(maid.getLastLocationLongitude());
                        nearby.setIsAvailable(maid.getIsAvailable());
                        nearby.setExperience(maid.getExperience());
                        nearby.setCity(maid.getCity());
                        nearbyMaids.add(nearby);
                    }
                } catch (Exception e) {
                    // Skip if distance calculation fails
                }
            }
        }

        // Sort by distance
        nearbyMaids.sort(Comparator.comparingDouble(NearbySearchDTO::getDistanceKm));

        return nearbyMaids;
    }

    /**
     * Get route between user and maid
     */
    public ORSLocationDTO getRouteToMaid(String userMobile, Long maidId) {
        return calculateUserMaidDistance(userMobile, maidId);
    }

    /**
     * Check if maid is within service area of user
     */
    public boolean isMaidWithinServiceArea(String userMobile, Long maidId, Double maxDistanceKm) {
        try {
            ORSLocationDTO distance = calculateUserMaidDistance(userMobile, maidId);
            return distance.getDistanceKm() <= maxDistanceKm;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculate travel time between user and maid
     */
    public Double calculateTravelTime(String userMobile, Long maidId) {
        ORSLocationDTO distance = calculateUserMaidDistance(userMobile, maidId);
        return distance.getDurationMinutes();
    }
}