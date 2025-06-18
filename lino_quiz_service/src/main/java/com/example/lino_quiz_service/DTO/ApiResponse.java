package com.example.lino_quiz_services.DTO;

public class ApiResponse<T> {
    private static String statusMessage;
    private int status;
    private T data;
    private Integer otp;

    public ApiResponse(int status, T data, String statusMessage) {
        this.status = status;
        this.data = data;
        this.statusMessage = statusMessage;
    }

    public ApiResponse(int status, Integer otp, String statusMessage) {
        this.status = status;
        this.otp = otp;
        this.statusMessage = statusMessage;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data, statusMessage);
    }
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, data, "Successfully created.");
    }

    public static ApiResponse<ErrorDetailsDTO> error(int status, String errorCode, String message) {
        return new ApiResponse<>(status, new ErrorDetailsDTO(message), errorCode);
    }
    public static ApiResponse<Void> email_success(Integer otp, String email, String statusMessage) {
        return new ApiResponse<>(200, otp, statusMessage);
    }

    public static ApiResponse<ErrorDetailsDTO> not_found(String message) {
        return new ApiResponse<>(404, new ErrorDetailsDTO(message), "NOT_FOUND");
    }

    public static ApiResponse<ErrorDetailsDTO> badRequest(String message) {
        return new ApiResponse<>(400, new ErrorDetailsDTO(message), "BAD_REQUEST");
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}