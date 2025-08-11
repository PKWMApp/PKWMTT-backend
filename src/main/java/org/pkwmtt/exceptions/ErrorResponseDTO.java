package org.pkwmtt.exceptions;

import lombok.*;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDTO {
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
