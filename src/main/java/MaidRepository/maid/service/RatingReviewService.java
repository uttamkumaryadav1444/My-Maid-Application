package MaidRepository.maid.service;

import MaidRepository.maid.dto.RatingReviewDTO.*;
import MaidRepository.maid.model.User;
import java.util.List;

public interface RatingReviewService {

    RatingResponseDTO createRating(User user, RatingRequestDTO request);

    List<RatingResponseDTO> getRatingsForMaid(Long maidId);

    List<UserHistoryDTO> getUserHistory(User user);

    List<MaidHistoryDTO> getMaidHistory(Long maidId);

    MaidRatingSummaryDTO getMaidRatingSummary(Long maidId);

    boolean canUserRateBooking(User user, Long bookingId);
}