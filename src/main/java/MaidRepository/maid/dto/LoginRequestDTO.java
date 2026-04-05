package MaidRepository.maid.dto;

public class LoginRequestDTO {
    private String username;
    private String password;
    private String userType;  // ✅ MUST be String, NOT Enum

    // Constructors
    public LoginRequestDTO() {}

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }  // ✅ Returns String
    public void setUserType(String userType) { this.userType = userType; }
}