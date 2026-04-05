package MaidRepository.maid.service;

import MaidRepository.maid.dto.*;
import java.util.List;

public interface LeaveService {

    // Maid applies for leave
    MaidLeaveDTO applyForLeave(LeaveRequestDTO request);

    // Get pending leaves for maid
    List<MaidLeaveDTO> getPendingLeaves(Long maidId);

    // Find replacement maids
    List<ReplacementMaidDTO> findReplacementMaids(Long leaveId, Double latitude, Double longitude, Double radiusKm);

    // Select replacement maid
    MaidLeaveDTO selectReplacement(Long leaveId, Long replacementMaidId, Long employerId);

    // Calculate salary with leave deduction
    SalaryCalculationDTO calculateSalary(Long maidId, Integer month, Integer year);

    // Approve leave (by admin/employer)
    MaidLeaveDTO approveLeave(Long leaveId, Long replacementMaidId);

    // Reject leave
    MaidLeaveDTO rejectLeave(Long leaveId, String reason);
}