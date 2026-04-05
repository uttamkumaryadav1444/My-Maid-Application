// AuthService.java - Complete with User and Maid OTP verification
package MaidRepository.maid.service;

import MaidRepository.maid.dto.*;
import MaidRepository.maid.model.User;
import MaidRepository.maid.model.Maid;
import MaidRepository.maid.model.User.AccountStatus;
import MaidRepository.maid.model.Maid.MaidStatus;

public interface AuthService {

    // User Registration with OTP
    UserResponseDTO registerUser(UserRequestDTO request);

    // Maid Registration with OTP
    MaidResponseDTO registerMaid(MaidRequestDTO request);

    // Login
    LoginResponseDTO login(String username, String password, String userType);

    // Change Password
    boolean changePassword(ChangePasswordDTO request, String userType);

    // Verify OTP and Activate Account
    OTPVerificationResponseDTO verifyAndActivate(String mobile, String otp, String userType);
}