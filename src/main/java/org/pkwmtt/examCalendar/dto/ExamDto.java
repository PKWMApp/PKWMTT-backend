package org.pkwmtt.examCalendar.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.StudentGroup;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@RequiredArgsConstructor
@Builder
public class ExamDto {

    @NotBlank
    @Size(max = 255, message = "max size of field is 255")
    private final String title;

    @Size(max = 255, message = "max size of field is 255")
    private final String description;

    @Future(message = "Date must be in the future")
    @NotNull
    private final LocalDateTime date;

    @NotNull
    private final String examType;

    @NotBlank
    private final Set<String> examGroups;
}
