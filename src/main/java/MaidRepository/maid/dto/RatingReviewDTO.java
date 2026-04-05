package MaidRepository.maid.dto;

import java.time.LocalDateTime;

public class RatingReviewDTO {

    // Request DTO for creating rating
    public static class RatingRequestDTO {
        private Long bookingId;
        private Integer rating;
        private String comment;

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    // Response DTO
    public static class RatingResponseDTO {
        private Long id;
        private Long bookingId;
        private Long userId;
        private String userName;
        private Long maidId;
        private String maidName;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public Long getMaidId() { return maidId; }
        public void setMaidId(Long maidId) { this.maidId = maidId; }

        public String getMaidName() { return maidName; }
        public void setMaidName(String maidName) { this.maidName = maidName; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    // DTO for user history
    public static class UserHistoryDTO {
        private Long bookingId;
        private String maidName;
        private String maidProfilePhoto;
        private String serviceDate;
        private String startTime;
        private String status;
        private Double totalAmount;
        private Integer rating;
        private String review;
        private boolean canRate;

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getMaidName() { return maidName; }
        public void setMaidName(String maidName) { this.maidName = maidName; }

        public String getMaidProfilePhoto() { return maidProfilePhoto; }
        public void setMaidProfilePhoto(String maidProfilePhoto) { this.maidProfilePhoto = maidProfilePhoto; }

        public String getServiceDate() { return serviceDate; }
        public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getReview() { return review; }
        public void setReview(String review) { this.review = review; }

        public boolean isCanRate() { return canRate; }
        public void setCanRate(boolean canRate) { this.canRate = canRate; }
    }

    // DTO for maid history
    public static class MaidHistoryDTO {
        private Long bookingId;
        private String userName;
        private String userMobile;
        private String serviceDate;
        private String startTime;
        private String endTime;
        private String status;
        private Double totalAmount;
        private String location;
        private Integer rating;
        private String review;

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getUserMobile() { return userMobile; }
        public void setUserMobile(String userMobile) { this.userMobile = userMobile; }

        public String getServiceDate() { return serviceDate; }
        public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getReview() { return review; }
        public void setReview(String review) { this.review = review; }
    }

    // DTO for maid rating summary
    public static class MaidRatingSummaryDTO {
        private Double averageRating;
        private Long totalReviews;

        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

        public Long getTotalReviews() { return totalReviews; }
        public void setTotalReviews(Long totalReviews) { this.totalReviews = totalReviews; }
    }
}