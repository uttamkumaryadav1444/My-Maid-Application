package MaidRepository.maid.dto;

import MaidRepository.maid.model.Language;
import java.util.Set;

public class UserUpdateRequestDTO {
    private String fullName;
    private String email;
    private String city;
    private String profilePhotoUrl;

    // ✅ NEW: Languages field
    private Set<Language> languages;

    public UserUpdateRequestDTO() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    // ✅ NEW Getters/Setters
    public Set<Language> getLanguages() { return languages; }
    public void setLanguages(Set<Language> languages) { this.languages = languages; }
}