// OTPVerificationResponseDTO.java
package MaidRepository.maid.dto;

public class OTPVerificationResponseDTO {
    private boolean success;
    private String message;
    private String mobile;
    private String userType;
    private boolean isVerified;
    private String redirectTo;
    private Long userId;
    private String name;

    public OTPVerificationResponseDTO() {}

    public OTPVerificationResponseDTO(boolean success, String message, String mobile,
                                      String userType, boolean isVerified, String redirectTo) {
        this.success = success;
        this.message = message;
        this.mobile = mobile;
        this.userType = userType;
        this.isVerified = isVerified;
        this.redirectTo = redirectTo;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public String getRedirectTo() { return redirectTo; }
    public void setRedirectTo(String redirectTo) { this.redirectTo = redirectTo; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}