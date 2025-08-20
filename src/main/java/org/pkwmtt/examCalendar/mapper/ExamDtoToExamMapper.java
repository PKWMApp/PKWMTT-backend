package org.pkwmtt.examCalendar.mapper;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.pkwmtt.examCalendar.repository.GroupRepository;
import org.pkwmtt.exceptions.ExamTypeNotExistsException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * maps ExamDto to Exam entity. Couldn't be utility class, because needs ExamTypeRepository to validate exam types
 */
@Component
@RequiredArgsConstructor
public class ExamDtoToExamMapper {
    private final ExamTypeRepository examTypeRepository;
    private final GroupRepository groupRepository;

    /**
     * @param examDto examDto object received from request
     * @return Exam entity WITHOUT examId which should be assigned by database
     *         Also contains examType field converted from String do ExamType
     */
    public Exam mapToNewExam(ExamDto examDto) {
        return Exam.builder()
                .title(examDto.getTitle())
                .description(examDto.getDescription())
                .examDate(examDto.getDate())
                .examType(examTypeRepository.findByName(examDto.getExamType()).orElseThrow(() -> new ExamTypeNotExistsException(examDto.getExamType())))
                .groups(groupRepository.findAllByNameIn(examDto.getExamGroups()))       //TODO: validate if groups is not null
                .build();
    }

    /**
     * @param examDto examDto object received from request
     * @param id of Exam that need to be modified
     * @return Exam entity WITH examId that allow to update entity in database instead of creating new one
     *         Also contains examType field converted from String do ExamType
     */
    public Exam mapToExistingExam(ExamDto examDto, int id) {
        return Exam.builder()
                .examId(id)
                .title(examDto.getTitle())
                .description(examDto.getDescription())
                .examDate(examDto.getDate())
                .examType(examTypeRepository.findByName(examDto.getExamType()).orElseThrow(() -> new ExamTypeNotExistsException(examDto.getExamType())))
                .groups(groupRepository.findAllByNameIn(examDto.getExamGroups()))
                .build();
    }
}
