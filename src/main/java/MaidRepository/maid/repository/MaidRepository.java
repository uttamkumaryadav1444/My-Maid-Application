package MaidRepository.maid.repository;

import MaidRepository.maid.model.Maid;
import MaidRepository.maid.model.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaidRepository extends JpaRepository<Maid, Long> {

    // ==================== EXISTING METHODS (from your original file) ====================

    Optional<Maid> findByMobile(String mobile);
    Optional<Maid> findByEmail(String email);
    List<Maid> findByIsAvailableTrue();

    // ✅ YEH LINE CRITICAL HAI – STRING PARAMETER
    List<Maid> findByServiceTypeAndIsAvailableTrue(String serviceType);

    List<Maid> findByServiceType(ServiceType serviceType);
    List<Maid> findByCity(String city);
    List<Maid> findByCityAndServiceType(String city, ServiceType serviceType);
    List<Maid> findByNameContainingIgnoreCase(String name);
    List<Maid> findBySkillsContainingIgnoreCase(String skill);
    List<Maid> findByIsVerifiedTrue();
    List<Maid> findByExperienceGreaterThanEqual(Integer minExperience);
    List<Maid> findByHourlyRateBetween(Double minRate, Double maxRate);
    boolean existsByMobile(String mobile);
    boolean existsByEmail(String email);

    /**
     * Find all available maids (isAvailable = true) that match the given service type and city.
     * Uses explicit JPQL query for better control.
     *
     * @param serviceType the service type enum value
     * @param city        the city name
     * @return list of matching maids
     */
    @Query("SELECT m FROM Maid m WHERE m.serviceType = :serviceType AND m.city = :city AND m.isAvailable = true")
    List<Maid> findAvailableByServiceTypeAndCity(@Param("serviceType") ServiceType serviceType,
                                                 @Param("city") String city);

    // ==================== ADVANCED SEARCH WITH DYNAMIC CRITERIA (native query with SpEL) ====================

    /**
     * DTO that holds all possible search criteria for maids.
     * (Can be placed in a separate package; shown here as inner class for completeness.)
     */
    class MaidSearchCriteria {
        private String serviceType;          // e.g., "HOUSEKEEPING"
        private String city;
        private String locality;
        private Integer minExperience;
        private Double maxHourlyRate;
        private Boolean isAvailable;
        private Boolean isVerified;
        private Double minRating;
        private Double latitude;              // user's current latitude for nearby search
        private Double longitude;              // user's current longitude for nearby search
        private Double radiusKm;                // search radius (km)
        private LocalDate date;                 // requested service date
        private LocalTime time;                  // requested start time
        private Integer durationHours;           // requested duration
        // Getters and setters (omitted for brevity – generate as needed)
    }

    /**
     * Advanced search for maids with multiple optional filters.
     * Uses a native query with SpEL expressions to conditionally include filters.
     *
     * @param criteria the search criteria (all fields optional)
     * @param pageable pagination information
     * @return page of maids matching the criteria
     */
    @Query(value = "SELECT m.* FROM maids m WHERE "
            // Service type filter (case‑insensitive comparison with enum name)
            + "(:#{#criteria.serviceType == null} OR UPPER(m.service_type) = UPPER(:#{#criteria.serviceType})) AND "
            // City filter
            + "(:#{#criteria.city == null} OR m.city = :#{#criteria.city}) AND "
            // Locality filter
            + "(:#{#criteria.locality == null} OR m.locality = :#{#criteria.locality}) AND "
            // Minimum experience
            + "(:#{#criteria.minExperience == null} OR m.experience >= :#{#criteria.minExperience}) AND "
            // Maximum hourly rate
            + "(:#{#criteria.maxHourlyRate == null} OR m.hourly_rate <= :#{#criteria.maxHourlyRate}) AND "
            // Availability flag
            + "(:#{#criteria.isAvailable == null} OR m.is_available = :#{#criteria.isAvailable}) AND "
            // Verified flag
            + "(:#{#criteria.isVerified == null} OR m.is_verified = :#{#criteria.isVerified}) AND "
            // Minimum average rating
            + "(:#{#criteria.minRating == null} OR m.average_rating >= :#{#criteria.minRating}) "

            // Distance‑based filtering (only if latitude, longitude, and radius are provided)
            + "AND (:#{#criteria.latitude == null} OR :#{#criteria.longitude == null} OR :#{#criteria.radiusKm == null} "
            + "   OR (6371 * acos(cos(radians(:#{#criteria.latitude})) "
            + "            * cos(radians(m.latitude)) "
            + "            * cos(radians(m.longitude) - radians(:#{#criteria.longitude})) "
            + "            + sin(radians(:#{#criteria.latitude})) * sin(radians(m.latitude)))) <= :#{#criteria.radiusKm}) "

            // Availability for a specific date/time (if provided) – checks for conflicting bookings
            + "AND (:#{#criteria.date == null} OR :#{#criteria.time == null} OR :#{#criteria.durationHours == null} "
            + "   OR NOT EXISTS (SELECT 1 FROM bookings b "
            + "                 WHERE b.maid_id = m.id "
            + "                   AND b.service_date = :#{#criteria.date} "
            + "                   AND b.status IN ('PENDING', 'ACCEPTED') "
            + "                   AND (b.start_time < ADDTIME(:#{#criteria.time}, SEC_TO_TIME(:#{#criteria.durationHours}*3600)) "
            + "                        AND ADDTIME(b.start_time, SEC_TO_TIME(b.duration_hours*3600)) > :#{#criteria.time}))) ",
            countQuery = "SELECT COUNT(*) FROM maids m WHERE "
                    + "(:#{#criteria.serviceType == null} OR UPPER(m.service_type) = UPPER(:#{#criteria.serviceType})) AND "
                    + "(:#{#criteria.city == null} OR m.city = :#{#criteria.city}) AND "
                    + "(:#{#criteria.locality == null} OR m.locality = :#{#criteria.locality}) AND "
                    + "(:#{#criteria.minExperience == null} OR m.experience >= :#{#criteria.minExperience}) AND "
                    + "(:#{#criteria.maxHourlyRate == null} OR m.hourly_rate <= :#{#criteria.maxHourlyRate}) AND "
                    + "(:#{#criteria.isAvailable == null} OR m.is_available = :#{#criteria.isAvailable}) AND "
                    + "(:#{#criteria.isVerified == null} OR m.is_verified = :#{#criteria.isVerified}) AND "
                    + "(:#{#criteria.minRating == null} OR m.average_rating >= :#{#criteria.minRating}) "
                    + "AND (:#{#criteria.latitude == null} OR :#{#criteria.longitude == null} OR :#{#criteria.radiusKm == null} "
                    + "   OR (6371 * acos(cos(radians(:#{#criteria.latitude})) * cos(radians(m.latitude)) "
                    + "            * cos(radians(m.longitude) - radians(:#{#criteria.longitude})) "
                    + "            + sin(radians(:#{#criteria.latitude})) * sin(radians(m.latitude)))) <= :#{#criteria.radiusKm}) "
                    + "AND (:#{#criteria.date == null} OR :#{#criteria.time == null} OR :#{#criteria.durationHours == null} "
                    + "   OR NOT EXISTS (SELECT 1 FROM bookings b WHERE b.maid_id = m.id AND b.service_date = :#{#criteria.date} "
                    + "                 AND b.status IN ('PENDING', 'ACCEPTED') "
                    + "                 AND (b.start_time < ADDTIME(:#{#criteria.time}, SEC_TO_TIME(:#{#criteria.durationHours}*3600)) "
                    + "                      AND ADDTIME(b.start_time, SEC_TO_TIME(b.duration_hours*3600)) > :#{#criteria.time})))",
            nativeQuery = true)
    Page<Maid> findMaidsByCriteria(@Param("criteria") MaidSearchCriteria criteria, Pageable pageable);
}