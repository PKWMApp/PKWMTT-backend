package org.pkwmtt.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class ErrorResponseDTO {
    private String message;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponseDTO(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
