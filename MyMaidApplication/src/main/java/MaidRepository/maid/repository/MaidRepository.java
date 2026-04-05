package MaidRepository.maid.repository;

import MaidRepository.maid.model.Language;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.model.Maid.WorkType;
import MaidRepository.maid.model.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaidRepository extends JpaRepository<Maid, Long> {

    // ✅ Basic CRUD - Return Optional
    Optional<Maid> findByMobile(String mobile);
    Optional<Maid> findByEmail(String email);
    boolean existsByMobile(String mobile);
    boolean existsByEmail(String email);

    // ✅ Filter methods
    List<Maid> findByIsAvailableTrue();
    List<Maid> findByServiceType(ServiceType serviceType);
    List<Maid> findByServiceTypeAndIsAvailableTrue(ServiceType serviceType);
    List<Maid> findByCity(String city);
    List<Maid> findByCityAndServiceType(String city, ServiceType serviceType);
    List<Maid> findByNameContainingIgnoreCase(String name);
    List<Maid> findBySkillsContainingIgnoreCase(String skill);
    List<Maid> findByIsVerifiedTrue();
    List<Maid> findByExperienceGreaterThanEqual(Integer minExperience);
    List<Maid> findByHourlyRateBetween(Double minRate, Double maxRate);

    // ✅ Language filter
    @Query("SELECT m FROM Maid m WHERE :language MEMBER OF m.languages")
    List<Maid> findByLanguage(@Param("language") Language language);

    // ✅ Advanced search with city and service type
    @Query("SELECT m FROM Maid m WHERE m.serviceType = :serviceType AND m.city = :city AND m.isAvailable = true")
    List<Maid> findAvailableByServiceTypeAndCity(@Param("serviceType") ServiceType serviceType,
                                                 @Param("city") String city);

    // ==================== ADVANCED SEARCH QUERY ====================
    @Query(value = "SELECT DISTINCT m.* FROM maids m " +
            "LEFT JOIN maid_languages ml ON m.id = ml.maid_id " +
            "WHERE m.status = 'ACTIVE' " +
            "AND (:city IS NULL OR m.city = :city) " +
            "AND (:serviceType IS NULL OR m.service_type = :serviceType) " +
            "AND (:minHourlyRate IS NULL OR m.hourly_rate >= :minHourlyRate) " +
            "AND (:maxHourlyRate IS NULL OR m.hourly_rate <= :maxHourlyRate) " +
            "AND (:isAvailable IS NULL OR m.is_available = :isAvailable) " +
            "AND (:minExperience IS NULL OR m.experience >= :minExperience) " +
            "AND (:maxExperience IS NULL OR m.experience <= :maxExperience) " +
            "AND (:language IS NULL OR ml.language = :language) " +
            "AND (:isVerified IS NULL OR m.is_verified = :isVerified) " +
            "AND (:minRating IS NULL OR m.average_rating >= :minRating) " +
            "AND (:workType IS NULL OR m.work_type = :workType) " +
            "AND (:latitude IS NULL OR :longitude IS NULL OR :radiusKm IS NULL OR " +
            "     (6371 * acos(cos(radians(:latitude)) * cos(radians(m.latitude)) * " +
            "     cos(radians(m.longitude) - radians(:longitude)) + " +
            "     sin(radians(:latitude)) * sin(radians(m.latitude)))) <= :radiusKm)",
            nativeQuery = true)
    Page<Maid> advancedSearch(
            @Param("city") String city,
            @Param("serviceType") String serviceType,
            @Param("minExperience") Integer minExperience,
            @Param("maxExperience") Integer maxExperience,
            @Param("minRating") Double minRating,
            @Param("minHourlyRate") Double minHourlyRate,
            @Param("maxHourlyRate") Double maxHourlyRate,
            @Param("isAvailable") Boolean isAvailable,
            @Param("language") String language,
            @Param("isVerified") Boolean isVerified,
            @Param("workType") String workType,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable);


    @Query("SELECT m FROM Maid m WHERE " +
            "(:city IS NULL OR m.city = :city) AND " +
            "(:serviceType IS NULL OR m.serviceType = :serviceType) AND " +
            "(:minExperience IS NULL OR m.experience >= :minExperience) AND " +
            "(:minRating IS NULL OR m.averageRating >= :minRating) AND " +
            "m.status = 'ACTIVE'")
    Page<Maid> freeSearch(
            @Param("city") String city,
            @Param("serviceType") ServiceType serviceType,
            @Param("minExperience") Integer minExperience,
            @Param("minRating") Double minRating,
            Pageable pageable);
}


