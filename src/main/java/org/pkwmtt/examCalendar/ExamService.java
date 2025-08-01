package org.pkwmtt.examCalendar;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.mapper.ExamDtoToExamMapper;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamDtoToExamMapper examMapper;

    /**
     * @param examDto details of exam
     * @return id of exam added to database
     */
    public int addExam(ExamDto examDto) {
        return examRepository.save(examMapper.mapToNewExam(examDto)).getExamId();
    }

    /**
     * @param examDto new details of exam that overwrite old ones
     * @param id of exam that need to be modified
     */
    public void modifyExam(ExamDto examDto, int id) {
        examRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Exam not found"));        //TODO: change exception type
        examRepository.save(examMapper.mapToExistingExam(examDto, id));
    }

}
