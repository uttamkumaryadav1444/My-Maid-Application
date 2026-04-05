package MaidRepository.maid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequestDTO {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number. Must be 10 digits starting with 6-9")
    private String mobile;

    @NotBlank(message = "OTP is required")
    @Size(min = 4, max = 6, message = "OTP must be between 4 and 6 digits")
    @Pattern(regexp = "^[0-9]+$", message = "OTP must contain only digits")
    private String otp;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,20}$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, one special character (@#$%^&+=) and no spaces")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "USER|MAID", message = "User type must be either 'USER' or 'MAID'")
    private String userType;

    // Constructors
    public ResetPasswordRequestDTO() {}

    public ResetPasswordRequestDTO(String mobile, String otp, String newPassword, String confirmPassword, String userType) {
        this.mobile = mobile;
        this.otp = otp;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
        this.userType = userType;
    }

    // Getters and Setters
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    // Validation method for controller
    public boolean isValid() {
        return mobile != null && !mobile.isEmpty() &&
                otp != null && !otp.isEmpty() &&
                newPassword != null && !newPassword.isEmpty() &&
                confirmPassword != null && !confirmPassword.isEmpty() &&
                newPassword.equals(confirmPassword) &&
                userType != null && !userType.isEmpty() &&
                (userType.equals("USER") || userType.equals("MAID"));
    }

    @Override
    public String toString() {
        return "ResetPasswordRequestDTO{" +
                "mobile='" + mobile + '\'' +
                ", otp='****'" +
                ", userType='" + userType + '\'' +
                '}';
    }
}