package MaidRepository.maid.dto;

import MaidRepository.maid.model.Maid.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class MaidRegistrationDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dob;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String mobile;

    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;

    // Constructors
    public MaidRegistrationDTO() {}

    public MaidRegistrationDTO(String name, Gender gender, LocalDate dob, String mobile,
                               String email, String password, String otp) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.otp = otp;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    // toString method
    @Override
    public String toString() {
        return "MaidRegistrationDTO{" +
                "name='" + name + '\'' +
                ", gender=" + gender +
                ", dob=" + dob +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", otp='[PROTECTED]'" +
                '}';
    }
}