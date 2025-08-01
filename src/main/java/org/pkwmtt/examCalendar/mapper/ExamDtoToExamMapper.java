package org.pkwmtt.examCalendar.mapper;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExamDtoToExamMapper {
    private final ExamTypeRepository examTypeRepository;

    /**
     * @param examDto examDto object received from request
     * @return Exam entity with examType field converted from String do ExamType
     */
    public Exam mapToExam(ExamDto examDto) {
        return Exam.builder()
                .title(examDto.getTitle())
                .description(examDto.getDescription())
                .date(examDto.getDate())
                .examGroups(examDto.getExamGroup())
                .examType(examTypeRepository.findByName(examDto.getExamType()).orElseThrow())
                .build();
    }
}
