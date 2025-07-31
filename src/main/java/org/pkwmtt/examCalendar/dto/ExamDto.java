package org.pkwmtt.examCalendar.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.ExamType;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@RequiredArgsConstructor
public class ExamDto {

    private final String title;
    private final String description;
    private final LocalDateTime date;
    private final String exam_group;
    private final ExamType exam_type;
}
