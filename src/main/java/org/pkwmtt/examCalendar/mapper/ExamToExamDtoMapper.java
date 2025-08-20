package org.pkwmtt.examCalendar.mapper;

import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.StudentGroup;

import java.util.Set;
import java.util.stream.Collectors;

public class ExamToExamDtoMapper {
    private ExamToExamDtoMapper() {}

    public static Set<ExamDto> mapToExamDto(Set<Exam> exams) {
        return exams.stream().map(ExamToExamDtoMapper::mapToExamDto).collect(Collectors.toSet());
    }

    public static ExamDto mapToExamDto(Exam exam) {
        return ExamDto.builder()
                .title(exam.getTitle())
                .description(exam.getDescription())
                .date(exam.getExamDate())
                .examType(exam.getExamType().getName())
                .examGroups(exam.getGroups().stream().map(StudentGroup::getName).collect(Collectors.toSet()))
                .build();
    }
}
