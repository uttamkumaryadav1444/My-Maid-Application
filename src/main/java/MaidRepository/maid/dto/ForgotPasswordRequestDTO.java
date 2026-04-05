package MaidRepository.maid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ForgotPasswordRequestDTO {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String mobile;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "USER|MAID", message = "User type must be USER or MAID")
    private String userType;  // ✅ Changed from Enum to String

    // Constructors
    public ForgotPasswordRequestDTO() {}

    public ForgotPasswordRequestDTO(String mobile, String userType) {
        this.mobile = mobile;
        this.userType = userType;
    }

    // Getters and Setters
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}