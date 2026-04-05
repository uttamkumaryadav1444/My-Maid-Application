package MaidRepository.maid.dto;

public class APIResponse {
    private boolean success;
    private String message;
    private Object data;
    private String errorCode;  // ✅ ADD THIS FIELD

    // Constructors
    public APIResponse() {}

    public APIResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public APIResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // ✅ ADD THIS CONSTRUCTOR
    public APIResponse(boolean success, String message, Object data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    // Builder Pattern Inner Class
    public static class Builder {
        private boolean success;
        private String message;
        private Object data;
        private String errorCode;  // ✅ ADD THIS

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        // ✅ ADD THIS METHOD
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public APIResponse build() {
            return new APIResponse(success, message, data, errorCode);
        }
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    // ✅ ADD GETTER AND SETTER FOR errorCode
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    // Static builder methods
    public static Builder builder() {
        return new Builder();
    }

    public static APIResponse success(String message) {
        return new APIResponse(true, message);
    }

    public static APIResponse success(String message, Object data) {
        return new APIResponse(true, message, data);
    }

    public static APIResponse error(String message) {
        return new APIResponse(false, message);
    }

    // ✅ ADD THIS METHOD - 2 PARAMETERS WALA
    public static APIResponse error(String message, String errorCode) {
        APIResponse response = new APIResponse(false, message);
        response.setErrorCode(errorCode);
        return response;
    }
}