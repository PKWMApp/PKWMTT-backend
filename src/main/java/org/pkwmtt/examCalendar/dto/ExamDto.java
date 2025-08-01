package org.pkwmtt.examCalendar.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ExamDto {

    private final String title;
    private final String description;
    private final LocalDateTime date;
    private final String examGroup;
    private final String examType;
}
