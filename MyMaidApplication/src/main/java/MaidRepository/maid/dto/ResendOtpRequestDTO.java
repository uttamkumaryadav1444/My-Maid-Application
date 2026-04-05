package MaidRepository.maid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResendOtpRequestDTO {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String mobile;

    // Constructors
    public ResendOtpRequestDTO() {}

    public ResendOtpRequestDTO(String mobile) {
        this.mobile = mobile;
    }

    // Getters and Setters
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
}