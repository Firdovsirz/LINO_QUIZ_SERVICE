package com.example.lino_quiz_services.DTO;

public class ErrorDetailsDTO {
    private String message;

    public ErrorDetailsDTO(String message) {
    }

    public void ErrorDetails(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
