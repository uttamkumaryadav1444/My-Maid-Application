package MaidRepository.maid.dto;

import lombok.Data;

@Data
public class LogoutRequestDTO {
    private String mobile;
    private String userType;
    private String token;

    // Optional - agar sab devices se logout karna ho
    private Boolean logoutFromAllDevices = false;
}