package org.pkwmtt.examCalendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.pkwmtt.examCalendar.mapper.ExamDtoMapper;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.pkwmtt.examCalendar.repository.GroupRepository;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamTypeRepository examTypeRepository;
    private final GroupRepository groupRepository;
    private final TimetableService timetableService;

    /**
     * @param examDto details of exam
     * @return id of exam added to database
     */
    public int addExam(ExamDto examDto) throws JsonProcessingException {

        verifyAndUpdateExamGroups(examDto);
        Set<StudentGroup> groups = groupRepository.findAllByNameIn(examDto.getExamGroups());

//        check if exam type exists
        ExamType examType = examTypeRepository.findByName(examDto.getExamType())
                .orElseThrow(() -> new ExamTypeNotExistsException(examDto.getExamType()));

//        save exam in repository and return id of created exam
        return examRepository.save(ExamDtoMapper.mapToNewExam(examDto, groups, examType)).getExamId();
    }

    /**
     * @param examDto new details of exam that overwrite old ones
     * @param id      of exam that need to be modified
     */
    public void modifyExam(ExamDto examDto, int id) throws JsonProcessingException {
//        check if exam which would be modified exists
        examRepository.findById(id).orElseThrow(() -> new NoSuchElementWithProvidedIdException(id));

        verifyAndUpdateExamGroups(examDto);
        Set<StudentGroup> groups = groupRepository.findAllByNameIn(examDto.getExamGroups());

//      check if exam type exists
        ExamType examType = examTypeRepository.findByName(examDto.getExamType())
                .orElseThrow(() -> new ExamTypeNotExistsException(examDto.getExamType()));

        examRepository.save(ExamDtoMapper.mapToExistingExam(examDto, groups, examType, id));
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

    public Set<Exam> getExamByGroups(Set<String> groupNames) {
//        validate provided groups
        Set<StudentGroup> studentGroups = groupRepository.findAllByNameIn(groupNames);
        Set<String> groupNamesFromDatabase = studentGroups.stream().map(StudentGroup::getName).collect(Collectors.toSet());
        if (!groupNamesFromDatabase.equals(groupNames)) {
            groupNames.removeAll(groupNamesFromDatabase);
            throw new InvalidGroupIdentifierException(groupNames);
        }
        return examRepository.findByGroupsIn(studentGroups);
    }

    /**
     * @return list of examTypes
     */
    public List<ExamType> getExamTypes() {
        return examTypeRepository.findAll();
    }


    private Set<String> getGroupsFromTimetableService() throws JsonProcessingException {
        List<String> generalGroups = timetableService.getGeneralGroupList();
        Set<String> allGroups = new HashSet<>(generalGroups);
        for (String groupName : generalGroups)
            allGroups.addAll(timetableService.getAvailableSubGroups(groupName));
        return allGroups;
    }

    /**
     * verify if groups exists in timetable if exist updates database.
     * when timetable service is unavailable check groups of existing exams for verification
     * @param examDto
     */
    private void verifyAndUpdateExamGroups(ExamDto examDto) throws JsonProcessingException {
        Set<String> allGroups;
        try {
            allGroups = getGroupsFromTimetableService();
            if (!allGroups.containsAll(examDto.getExamGroups())) {
                examDto.getExamGroups().removeAll(allGroups);
                throw new InvalidGroupIdentifierException(examDto.getExamGroups());
            } else
                groupRepository.saveAll(examDto.getExamGroups().stream()
                        .map(g -> StudentGroup.builder()
                                .name(g)
                                .build())
                        .collect(Collectors.toSet())
                );
        } catch (JsonProcessingException | SpecifiedGeneralGroupDoesntExistsException | WebPageContentNotAvailableException e) {
            allGroups = groupRepository.findAllByNameIn(examDto.getExamGroups())
                    .stream()
                    .map(StudentGroup::getName)
                    .collect(Collectors.toSet());
            if (!allGroups.containsAll(examDto.getExamGroups()))
                throw e;
        }
    }
}
