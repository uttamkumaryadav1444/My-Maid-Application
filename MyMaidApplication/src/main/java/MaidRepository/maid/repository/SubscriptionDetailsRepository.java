package MaidRepository.maid.repository;

import MaidRepository.maid.model.SubscriptionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubscriptionDetailsRepository extends JpaRepository<SubscriptionDetails, Integer> {

    List<SubscriptionDetails> findByType(String type);

    @Query("SELECT s FROM SubscriptionDetails s ORDER BY s.amount ASC")
    List<SubscriptionDetails> findAllOrderByAmount();

    @Query("SELECT s.cuponCode FROM Subscriber s WHERE s.cuponCode = :cuponCode")
    String findCuponCode(@Param("cuponCode") String cuponCode);

    @Query("SELECT s FROM SubscriptionDetails s")
    List<SubscriptionDetails> findDetails();
}