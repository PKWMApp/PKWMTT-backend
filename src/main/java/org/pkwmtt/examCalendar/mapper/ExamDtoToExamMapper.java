package org.pkwmtt.examCalendar.mapper;

import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;

public class ExamDtoToExamMapper {
    private ExamDtoToExamMapper() {}
    public static Exam mapToExam(ExamDto examDto) {
        return Exam.builder()
                .title(examDto.getTitle())
                .description(examDto.getDescription())
                .date(examDto.getDate())
                .exam_group(examDto.getExam_group())
                .exam_type(examDto.getExam_type())
                .build();
    }
}
