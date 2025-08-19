package org.pkwmtt.examCalendar;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.mapper.ExamDtoToExamMapper;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.pkwmtt.exceptions.NoSuchElementWithProvidedIdException;
import org.pkwmtt.exceptions.UnsupportedCountOfArgumentsException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamDtoToExamMapper examMapper;
    private final ExamTypeRepository examTypeRepository;

    /**
     * @param examDto details of exam
     * @return id of exam added to database
     */
    public int addExam(ExamDto examDto) {
        return examRepository.save(examMapper.mapToNewExam(examDto)).getExamId();
    }

    /**
     * @param examDto new details of exam that overwrite old ones
     * @param id      of exam that need to be modified
     */
    public void modifyExam(ExamDto examDto, int id) {
        examRepository.findById(id).orElseThrow(() -> new NoSuchElementWithProvidedIdException(id));
        examRepository.save(examMapper.mapToExistingExam(examDto, id));
    }

    /**
     * @param id of exam
     */
    public void deleteExam(int id) {
        examRepository.findById(id).orElseThrow(() -> new NoSuchElementWithProvidedIdException(id));
        examRepository.deleteById(id);
    }

    /**
     * @param id of exam
     * @return exam
     */
    public Exam getExamById(int id) {
        return examRepository.findById(id).orElseThrow(() -> new NoSuchElementWithProvidedIdException(id));
    }

//    /**
//     * @param groups set od groups (max 4)
//     * @return set of exams for specific groups
//     */
//    public Set<Exam> getExamByGroup(Set<String> groups) {
//        if (groups.size() > 4 || groups.isEmpty())
//            throw new UnsupportedCountOfArgumentsException(1, 5, groups.size());
//        List<String> groupList = new ArrayList<>(groups);
//        return switch (groupList.size()) {
//            case 4 -> examRepository.findExamsByGroupsIdentifier(
//                    groupList.get(0), groupList.get(1), groupList.get(2), groupList.get(3));
//            case 3 -> examRepository.findExamsByGroupsIdentifier(
//                    groupList.get(0), groupList.get(1), groupList.get(2));
//            case 2 -> examRepository.findExamsByGroupsIdentifier(
//                    groupList.get(0), groupList.get(1));
//            case 1 -> examRepository.findExamsByGroupsIdentifier(
//                    groupList.get(0));
//            default -> Set.of();
//        };
//    }

    /**
     * @return list of examTypes
     */
    public List<ExamType> getExamTypes() {
        return examTypeRepository.findAll();
    }
}
