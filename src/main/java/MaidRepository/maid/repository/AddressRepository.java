// AddressRepository.java
package MaidRepository.maid.repository;

import MaidRepository.maid.model.Address;
import MaidRepository.maid.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUser(User user);

    Optional<Address> findByIdAndUser(Long id, User user);

    Optional<Address> findByUserAndIsCurrentTrue(User user);

    Optional<Address> findByUserAndIsDefaultTrue(User user);

    List<Address> findByUserOrderByIsCurrentDescCreatedAtDesc(User user);

    @Query("SELECT a FROM Address a WHERE a.user = :user AND (a.isCurrent = true OR a.isDefault = true)")
    List<Address> findPrimaryAddresses(@Param("user") User user);

    boolean existsByUserAndIsCurrentTrue(User user);
}