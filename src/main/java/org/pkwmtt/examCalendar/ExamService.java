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
    public int addExam(ExamDto examDto) {

        Set<StudentGroup> groups = verifyAndUpdateExamGroups(examDto);

        ExamType examType = examTypeRepository.findByName(examDto.getExamType())
                .orElseThrow(() -> new ExamTypeNotExistsException(examDto.getExamType()));

        Exam exam = ExamDtoMapper.mapToNewExam(examDto, groups, examType);
        Set<Exam> existingExam = examRepository.findAllByTitle(exam.getTitle());
        if (existingExam.contains(exam))
            throw new ResourceAlreadyExistsException("Exam already exists");
        return examRepository.save(exam).getExamId();
    }

    /**
     * @param examDto new details of exam that overwrite old ones
     * @param id      of exam that need to be modified
     */
    public void modifyExam(ExamDto examDto, int id) {

        examRepository.findById(id).orElseThrow(() -> new NoSuchElementWithProvidedIdException(id));

        Set<StudentGroup> groups = verifyAndUpdateExamGroups(examDto);

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

    public Set<Exam> getExamByGroups(Set<String> generalGroups, Set<String> subgroups) {
//        verify generalGroups identifiers
        verifyGeneralGroupsFormat(generalGroups);
//        get exams for general groups
        Set<Exam> exams = new HashSet<>(examRepository.findAllByGroups_NameIn(generalGroups));
        exams = exams.stream()
                .filter(exam -> exam.getGroups().stream()
                        .allMatch(group -> group.getName().matches("^\\d.*")))
                .collect(Collectors.toSet());

//        convert general group identifiers. e.g. 12K2 to 12K
        Set<String> superiorGroups = generalGroups.stream().map(g -> {
            if (Character.isDigit(g.charAt(g.length() - 1)))
                return g.substring(0, g.length() - 1);
            return g;
        }).collect(Collectors.toSet());
//        check if subgroups are provided
        if (subgroups != null && !subgroups.isEmpty()) {
//            verify subgroups identifiers
            verifySubgroupsFormat(subgroups);
//            check if superior group identifies the groups unambiguously
            if (superiorGroups.size() != 1)
                throw new InvalidGroupIdentifierException("ambiguous superior group identifier for subgroups");
            exams.addAll(examRepository.findAllBySubgroupsOfGeneralGroup(superiorGroups.iterator().next(), subgroups));
        }
        return exams;
    }

    /**
     * @return list of examTypes
     */
    public List<ExamType> getExamTypes() {
        return examTypeRepository.findAll();
    }

    /**
     * verify if groups exists in timetable if exist updates database.
     * when timetable service is unavailable verifies groups using groupsRepository
     *
     * @param examDto containing groups for verification
     */
    private Set<StudentGroup> verifyAndUpdateExamGroups(ExamDto examDto) {
        Set<String> generalGroups = examDto.getGeneralGroups();
        Set<String> subgroups = examDto.getSubgroups();

        if (generalGroups == null || generalGroups.isEmpty())
            throw new InvalidGroupIdentifierException("general group is missing");

        verifyGeneralGroups(generalGroups);

        if (subgroups == null || subgroups.isEmpty())
            return saveNewStudentGroups(generalGroups);

        if (generalGroups.size() > 1)
            throw new InvalidGroupIdentifierException("ambiguous general groups for subgroups");

        String superiorGroup = generalGroups.iterator().next();
        verifySubgroups(superiorGroup, subgroups);

        subgroups.add(trimLastDigit(superiorGroup));
        return saveNewStudentGroups(subgroups);
    }

    private void verifyGeneralGroups(Set<String> generalGroups) {
        try {
            Set<String> existingGeneralGroups = new HashSet<>(timetableService.getGeneralGroupList());
            if (!existingGeneralGroups.containsAll(generalGroups))
                throw new InvalidGroupIdentifierException(existingGeneralGroups, generalGroups);
        } catch (WebPageContentNotAvailableException e) {
            verifyGeneralGroupsUsingRepository(generalGroups);
        }
    }

    /**
     * @param groups groups that would be verified using repository
     * @throws WebPageContentNotAvailableException when verification not succeeded
     */
    private void verifyGeneralGroupsUsingRepository(Set<String> groups) throws WebPageContentNotAvailableException {
        verifyGeneralGroupsFormat(groups);
        Set<String> groupsFromRepository = groupRepository.findAllByNameIn(groups).stream()
                .map(StudentGroup::getName)
                .collect(Collectors.toSet()
                );
        if (!groupsFromRepository.containsAll(groups))
            throw new ServiceNotAvailableException("Couldn't verify groups using repository");
    }

    private void verifySubgroups(String superiorGroup, Set<String> subgroups) {
        try {
            Set<String> subGroupsFromTimetable = new HashSet<>(timetableService.getAvailableSubGroups(superiorGroup));
            if (!subGroupsFromTimetable.containsAll(subgroups))
                throw new InvalidGroupIdentifierException(subGroupsFromTimetable, subgroups);
        } catch (JsonProcessingException |
                 SpecifiedGeneralGroupDoesntExistsException |
                 WebPageContentNotAvailableException e) {
            throw new ServiceNotAvailableException("Couldn't verify groups using timetable service");
//                TODO: add verification with repository when timetable service is unavailable
        }
    }

    private static String trimLastDigit(String superiorGroup) {
        char lastChar = superiorGroup.charAt(superiorGroup.length() - 1);
        if (Character.isDigit(lastChar))
            superiorGroup = superiorGroup.substring(0, superiorGroup.length() - 1);
        return superiorGroup;
    }

    /**
     * saves groups to groupRepository, existing group names are filtered out before saving
     *
     * @param groups groups that would be saved to repository
     * @return set of StudentsGroup Entity with names from groups.
     */
    private Set<StudentGroup> saveNewStudentGroups(Set<String> groups) {
//        remove duplicates before saving records
        Set<StudentGroup> existingGroups = groupRepository.findAllByNameIn(groups);
        groups.removeAll(existingGroups.stream()
                .map(StudentGroup::getName)
                .collect(Collectors.toSet())
        );
        List<StudentGroup> savedGroups = groupRepository.saveAll(groups.stream()
                .map(g -> StudentGroup.builder()
                        .name(g)
                        .build()
                ).collect(Collectors.toList())
        );
        existingGroups.addAll(savedGroups);
        return existingGroups;
    }

    private static void verifyGeneralGroupsFormat(Set<String> generalGroups) throws SpecifiedGeneralGroupDoesntExistsException {
        generalGroups.forEach(group -> {
            if (!group.matches("^\\d.*"))
                throw new SpecifiedGeneralGroupDoesntExistsException(group);
        });
    }

    private static void verifySubgroupsFormat(Set<String> subgroups) throws SpecifiedSubGroupDoesntExistsException {
        subgroups.forEach(group -> {
            if (!group.matches("^[A-Z].*"))
                throw new SpecifiedSubGroupDoesntExistsException(group);
        });
    }
}
