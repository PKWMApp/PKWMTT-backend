package org.pkwmtt.examCalendar.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@RequiredArgsConstructor
@SuperBuilder
public class RequestExamDto {

    @NotBlank
    @Size(max = 255, message = "max size of field is 255")
    private String title;

    @Size(max = 255, message = "max size of field is 255")
    private String description;

    @Future(message = "Date must be in the future")
    @NotNull
    private LocalDateTime date;

    @NotNull
    private String examType;

    @NotEmpty
    @Size(min = 1)
    private Set<String> generalGroups;

    private Set<String> subgroups;
}
