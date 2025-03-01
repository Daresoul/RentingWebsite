package com.renting.rentingwebsite.DTO;

public class ErrorResponseDTO {
    private int status;
    private String message;
    private String timestamp;

    public ErrorResponseDTO(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
}