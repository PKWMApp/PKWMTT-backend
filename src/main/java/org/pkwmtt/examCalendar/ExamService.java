package org.pkwmtt.examCalendar;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.mapper.ExamDtoToExamMapper;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;

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

    /**
     * @param id of exam
     */
    public void deleteExam(int id) {
        examRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Exam not found"));        //TODO: change exception type
        examRepository.deleteById(id);
    }

    /**
     * @param id of exam
     * @return exam
     */
    public Exam getExamById(int id) {
        return examRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Exam not found"));        //TODO: change exception typ
    }

    /**
     * limited number of arguments but more efficient query to database
     * @param generalGroup symbol that identify exercise group of specific field of study (for example 12K2)
     * @param kGroup computer laboratory group (non required)
     * @param lGroup laboratory group (non required)
     * @param pGroup project group (non required)
     * @return Set of exams for specific groups
     */
    public Set<Exam> getExamByGroup(String generalGroup, String kGroup, String lGroup, String pGroup) {
//        TODO: change to 1 query instead of 4
//        TODO: change arguments to list
//        TODO: N + 1
        Set<Exam> exams = examRepository.findByExamGroupsContaining(generalGroup);
        if (kGroup != null) exams.addAll(examRepository.findByExamGroupsContaining(kGroup));
        if (lGroup != null) exams.addAll(examRepository.findByExamGroupsContaining(lGroup));
        if (pGroup != null) exams.addAll(examRepository.findByExamGroupsContaining(pGroup));
        return exams;
    }

    /**
     * flexible number of arguments but inefficient query to database
     * @param generalGroup symbol that identify exercise group of specific field of study (for example 12K2)
     * @param groups set od groups
     * @return set of exams for specific groups
     */
    public Set<Exam> getExamByGroupsSet(String generalGroup, Set<String> groups) {
//        TODO: change arguments to list
//        TODO: N + 1
        throw new UnsupportedOperationException("Not supported yet.");
//        Set<Exam> exams = examRepository.findByExamGroupsContaining(generalGroup);
//        if (kGroup != null) exams.addAll(examRepository.findByExamGroupsContaining(kGroup));
//        if (lGroup != null) exams.addAll(examRepository.findByExamGroupsContaining(lGroup));
//        if (pGroup != null) exams.addAll(examRepository.findByExamGroupsContaining(pGroup));
//        return exams;
    }
}
