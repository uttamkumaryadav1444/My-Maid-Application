package MaidRepository.maid.dto;

public class LoginResponseDTO {
    private Long id;
    private Long userId;        // ✅ ADD THIS
    private String name;
    private String mobile;
    private String email;
    private String userType;
    private String profilePhoto;
    private String city;
    private String token;
    private boolean profileComplete;  // ✅ ADD THIS
    private String redirectTo;        // ✅ ADD THIS

    // Constructors
    public LoginResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public boolean isProfileComplete() { return profileComplete; }
    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public String getRedirectTo() { return redirectTo; }
    public void setRedirectTo(String redirectTo) { this.redirectTo = redirectTo; }
}