package org.pkwmtt.examCalendar.mapper;

import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * maps ExamDto to Exam entity. Couldn't be utility class, because needs ExamTypeRepository to validate exam types
 */
@Component
@Slf4j
public class ExamDtoMapper {
    private ExamDtoMapper() {
        throw new IllegalStateException("Utility class");
    }

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

    public static List<ExamDto> mapToExamDto(Set<Exam> exams) {
        return exams.stream().map(ExamDtoMapper::mapToExamDto).collect(Collectors.toList());
    }

    public static ExamDto mapToExamDto(Exam exam) {
        Set<String> groups = exam.getGroups().stream().map(StudentGroup::getName).collect(Collectors.toSet());
        Set<String> generalGroups = groups.stream().filter(group -> Character.isDigit(group.charAt(0))).collect(Collectors.toSet());
        Set<String> subgroups = groups.stream().filter(group -> Character.isAlphabetic(group.charAt(0))).collect(Collectors.toSet());
        if(groups.size() != subgroups.size() + generalGroups.size())
            log.warn("Some groups of {} were not consumed in ExamDtoMapper.mapToExamDto()", groups);
        return ExamDto.builder()
                .title(exam.getTitle())
                .description(exam.getDescription())
                .date(exam.getExamDate())
                .examType(exam.getExamType().getName())
                .generalGroups(generalGroups)
                .subgroups(subgroups)
                .build();
    }
}
