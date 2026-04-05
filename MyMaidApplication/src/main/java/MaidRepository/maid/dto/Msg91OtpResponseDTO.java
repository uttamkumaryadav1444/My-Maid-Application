package MaidRepository.maid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Msg91OtpResponseDTO {

    private String type;
    private String message;

    @JsonProperty("request_id")
    private String requestId;

    private String otp;  // ✅ YEH FIELD HONA CHAHIYE

    private boolean success = true;

    // Constructors
    public Msg91OtpResponseDTO() {}

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

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "Msg91OtpResponseDTO{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", requestId='" + requestId + '\'' +
                ", otp='" + otp + '\'' +
                ", success=" + success +
                '}';
    }
}