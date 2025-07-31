package org.pkwmtt.examCalendar;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.mapper.ExamDtoToExamMapper;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository examRepository;

    public int addExam(ExamDto examDto) {
        return examRepository.save(ExamDtoToExamMapper.mapToExam(examDto)).getExam_id();
    }
}
