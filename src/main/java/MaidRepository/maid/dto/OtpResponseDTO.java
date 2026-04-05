// OtpResponseDTO.java
package MaidRepository.maid.dto;

import java.util.Map;

public class OtpResponseDTO {
    private boolean success;
    private String message;
    private Map<String, Object> data;
    private String errorCode;

    public OtpResponseDTO() {}

    public OtpResponseDTO(boolean success, String message, Map<String, Object> data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
}