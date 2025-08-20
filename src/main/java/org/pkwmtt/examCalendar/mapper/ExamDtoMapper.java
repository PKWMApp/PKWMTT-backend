package org.pkwmtt.examCalendar.mapper;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * maps ExamDto to Exam entity. Couldn't be utility class, because needs ExamTypeRepository to validate exam types
 */
@Component
@RequiredArgsConstructor
public class ExamDtoMapper {
    private ExamDtoMapper examDtoMapper;

    /**
     * @param examDto examDto object received from request
     * @return Exam entity WITHOUT examId which should be assigned by database
     *         Also contains examType field converted from String do ExamType
     */
    public static Exam mapToNewExam(ExamDto examDto, Set<StudentGroup> groups, ExamType examType) {
        return Exam.builder()
                .title(examDto.getTitle())
                .description(examDto.getDescription())
                .examDate(examDto.getDate())
                .examType(examType)
                .groups(groups)
                .build();
    }

    /**
     * @param examDto examDto object received from request
     * @param id of Exam that need to be modified
     * @return Exam entity WITH examId that allow to update entity in database instead of creating new one
     *         Also contains examType field converted from String do ExamType
     */
    public static Exam mapToExistingExam(ExamDto examDto, Set<StudentGroup> groups, ExamType examType, int id) {
        return Exam.builder()
                .examId(id)
                .title(examDto.getTitle())
                .description(examDto.getDescription())
                .examDate(examDto.getDate())
                .examType(examType)
                .groups(groups)
                .build();
    }

    public static Set<ExamDto> mapToExamDto(Set<Exam> exams) {
        return exams.stream().map(ExamDtoMapper::mapToExamDto).collect(Collectors.toSet());
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
