package MaidRepository.maid.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnlockContactResponseDTO {
    private Long maidId;
    private String message;
    private String mobile;
    private String email;

    // Getters and Setters
    public Long getMaidId() { return maidId; }
    public void setMaidId(Long maidId) { this.maidId = maidId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}