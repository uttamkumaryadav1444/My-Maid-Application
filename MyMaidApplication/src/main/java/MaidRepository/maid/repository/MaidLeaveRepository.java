package MaidRepository.maid.repository;

import MaidRepository.maid.model.MaidLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaidLeaveRepository extends JpaRepository<MaidLeave, Long> {

    List<MaidLeave> findByMaidId(Long maidId);

    List<MaidLeave> findByStatus(MaidLeave.LeaveStatus status);

    @Query("SELECT l FROM MaidLeave l WHERE l.maid.id = :maidId AND l.status = 'PENDING'")
    List<MaidLeave> findPendingLeavesByMaid(@Param("maidId") Long maidId);

    @Query("SELECT l FROM MaidLeave l WHERE l.startDate <= :date AND l.endDate >= :date AND l.status = 'APPROVED'")
    List<MaidLeave> findActiveLeavesOnDate(@Param("date") LocalDate date);

    @Query("SELECT l FROM MaidLeave l WHERE l.replacementMaid.id = :maidId AND l.status = 'APPROVED'")
    List<MaidLeave> findLeavesAsReplacement(@Param("maidId") Long maidId);

    @Query("SELECT l FROM MaidLeave l WHERE l.maid.id = :maidId AND l.status = 'APPROVED'")
    List<MaidLeave> findApprovedLeavesByMaid(@Param("maidId") Long maidId);
}