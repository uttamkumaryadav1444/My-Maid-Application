package MaidRepository.maid.impl;

import MaidRepository.maid.dto.RatingReviewDTO.*;
import MaidRepository.maid.model.*;
import MaidRepository.maid.model.Booking.BookingStatus;
import MaidRepository.maid.repository.*;
import MaidRepository.maid.service.RatingReviewService;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingReviewServiceImpl implements RatingReviewService {

    private final RatingReviewRepository ratingReviewRepository;
    private final BookingRepository bookingRepository;
    private final MaidRepository maidRepository;
    private final UserRepository userRepository;

    public RatingReviewServiceImpl(RatingReviewRepository ratingReviewRepository,
                                   BookingRepository bookingRepository,
                                   MaidRepository maidRepository,
                                   UserRepository userRepository) {
        this.ratingReviewRepository = ratingReviewRepository;
        this.bookingRepository = bookingRepository;
        this.maidRepository = maidRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RatingResponseDTO createRating(User user, RatingRequestDTO request) {
        try {
            System.out.println("Creating rating for user: " + user.getId() + ", booking: " + request.getBookingId());

            // Validate booking
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + request.getBookingId()));

            System.out.println("Found booking with status: " + booking.getStatus());

            // Check if user is the one who booked
            if (!booking.getUser().getId().equals(user.getId())) {
                System.out.println("User mismatch: Booking user ID=" + booking.getUser().getId() + ", Current user ID=" + user.getId());
                throw new RuntimeException("You can only rate your own bookings");
            }

            // Check if booking is completed
            if (booking.getStatus() != BookingStatus.COMPLETED) {
                throw new RuntimeException("You can only rate completed bookings. Current status: " + booking.getStatus());
            }

            // Check if already rated
            if (ratingReviewRepository.existsByUserIdAndBookingId(user.getId(), request.getBookingId())) {
                throw new RuntimeException("You have already rated this booking");
            }

            // Validate rating
            if (request.getRating() < 1 || request.getRating() > 5) {
                throw new RuntimeException("Rating must be between 1 and 5");
            }

            // Create rating
            RatingReview rating = new RatingReview();
            rating.setBooking(booking);
            rating.setUser(user);
            rating.setMaid(booking.getMaid());
            rating.setRating(request.getRating());
            rating.setComment(request.getComment());

            RatingReview savedRating = ratingReviewRepository.save(rating);
            System.out.println("Rating saved with ID: " + savedRating.getId());

            // Update maid's average rating
            updateMaidAverageRating(booking.getMaid().getId());

            return convertToRatingResponseDTO(savedRating);
        } catch (Exception e) {
            System.err.println("Error in createRating: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<RatingResponseDTO> getRatingsForMaid(Long maidId) {
        try {
            return ratingReviewRepository.findByMaidIdOrderByCreatedAtDesc(maidId)
                    .stream()
                    .map(this::convertToRatingResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getRatingsForMaid: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<UserHistoryDTO> getUserHistory(User user) {
        try {
            List<Booking> userBookings = bookingRepository.findByUser(user);
            System.out.println("Found " + userBookings.size() + " bookings for user: " + user.getId());

            return userBookings.stream().map(booking -> {
                UserHistoryDTO history = new UserHistoryDTO();
                history.setBookingId(booking.getId());
                history.setMaidName(booking.getMaid().getName());
                history.setMaidProfilePhoto(booking.getMaid().getProfilePhotoUrl());
                history.setServiceDate(booking.getServiceDate().toString());
                history.setStartTime(booking.getStartTime().toString());
                history.setStatus(booking.getStatus().toString());
                history.setTotalAmount(booking.getTotalAmount());

                // Check if rating exists
                RatingReview rating = ratingReviewRepository.findByBookingId(booking.getId()).orElse(null);
                if (rating != null) {
                    history.setRating(rating.getRating());
                    history.setReview(rating.getComment());
                    history.setCanRate(false);
                } else {
                    history.setCanRate(booking.getStatus() == BookingStatus.COMPLETED);
                }

                return history;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getUserHistory: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<MaidHistoryDTO> getMaidHistory(Long maidId) {
        try {
            Maid maid = maidRepository.findById(maidId)
                    .orElseThrow(() -> new RuntimeException("Maid not found"));

            List<Booking> maidBookings = bookingRepository.findByMaid(maid);

            return maidBookings.stream().map(booking -> {
                MaidHistoryDTO history = new MaidHistoryDTO();
                history.setBookingId(booking.getId());
                history.setUserName(booking.getCustomerName());
                history.setUserMobile(booking.getCustomerMobile());
                history.setServiceDate(booking.getServiceDate().toString());
                history.setStartTime(booking.getStartTime().toString());
                if (booking.getEndTime() != null) {
                    history.setEndTime(booking.getEndTime().toString());
                }
                history.setStatus(booking.getStatus().toString());
                history.setTotalAmount(booking.getTotalAmount());
                history.setLocation(booking.getLocation());

                // Check if rating exists
                RatingReview rating = ratingReviewRepository.findByBookingId(booking.getId()).orElse(null);
                if (rating != null) {
                    history.setRating(rating.getRating());
                    history.setReview(rating.getComment());
                }

                return history;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getMaidHistory: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public MaidRatingSummaryDTO getMaidRatingSummary(Long maidId) {
        try {
            MaidRatingSummaryDTO summary = new MaidRatingSummaryDTO();

            Double averageRating = ratingReviewRepository.findAverageRatingByMaidId(maidId);
            summary.setAverageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);

            Long totalReviews = ratingReviewRepository.countByMaidId(maidId);
            summary.setTotalReviews(totalReviews);

            return summary;
        } catch (Exception e) {
            System.err.println("Error in getMaidRatingSummary: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean canUserRateBooking(User user, Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            boolean isUserBooking = booking.getUser().getId().equals(user.getId());
            boolean isCompleted = booking.getStatus() == BookingStatus.COMPLETED;
            boolean notRated = !ratingReviewRepository.existsByUserIdAndBookingId(user.getId(), bookingId);

            return isUserBooking && isCompleted && notRated;
        } catch (Exception e) {
            System.err.println("Error in canUserRateBooking: " + e.getMessage());
            return false;
        }
    }

    private void updateMaidAverageRating(Long maidId) {
        try {
            Double averageRating = ratingReviewRepository.findAverageRatingByMaidId(maidId);
            Maid maid = maidRepository.findById(maidId)
                    .orElseThrow(() -> new RuntimeException("Maid not found"));

            maid.setAverageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);
            maidRepository.save(maid);
            System.out.println("Updated maid " + maidId + " average rating to: " + maid.getAverageRating());
        } catch (Exception e) {
            System.err.println("Error updating maid average rating: " + e.getMessage());
        }
    }

    private RatingResponseDTO convertToRatingResponseDTO(RatingReview rating) {
        RatingResponseDTO dto = new RatingResponseDTO();
        dto.setId(rating.getId());
        dto.setBookingId(rating.getBooking().getId());
        dto.setUserId(rating.getUser().getId());
        dto.setUserName(rating.getUser().getFullName());
        dto.setMaidId(rating.getMaid().getId());
        dto.setMaidName(rating.getMaid().getName());
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }
}