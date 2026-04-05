package MaidRepository.maid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VerifyOtpRequestDTO {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String mobile;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "USER|MAID", message = "User type must be USER or MAID")
    private String userType;  // ✅ Changed from Enum to String

    // Constructors
    public VerifyOtpRequestDTO() {}

    // Getters and Setters
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}