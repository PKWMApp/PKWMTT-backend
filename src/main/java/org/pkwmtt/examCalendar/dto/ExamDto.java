package org.pkwmtt.examCalendar.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ExamDto {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "max size of field is 255")
    private final String title;

    @Size(max = 255, message = "max size of field is 255")
    private final String description;

    @Future(message = "Date must be in the future")
    @NotNull
    private final LocalDateTime date;

    @NotBlank
    @Size(max = 255, message = "max size of field is 255")
    private final String examGroups;

    @NotNull
    private final String examType;
}
