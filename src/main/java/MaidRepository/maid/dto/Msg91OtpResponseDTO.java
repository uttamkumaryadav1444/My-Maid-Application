package MaidRepository.maid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Msg91OtpResponseDTO {

    private String type;
    private String message;

    @JsonProperty("request_id")
    private String requestId;

    // Default constructor
    public Msg91OtpResponseDTO() {}

    // Parameterized constructor
    public Msg91OtpResponseDTO(String type, String message, String requestId) {
        this.type = type;
        this.message = message;
        this.requestId = requestId;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    // Helper method to check success
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(type);
    }

    @Override
    public String toString() {
        return "Msg91OtpResponseDTO{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}