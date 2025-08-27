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
    public void modifyExam(ExamDto examDto, int id) {
//        check if exam which would be modified exists
        examRepository.findById(id).orElseThrow(() -> new NoSuchElementWithProvidedIdException(id));

        Set<StudentGroup> groups = verifyAndUpdateExamGroups(examDto);

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

    public List<Exam> getExamByGroups(Set<String> generalGroups, Set<String> subgroups) {
//        get exams for general groups
        List<Exam> exams = new ArrayList<>(examRepository.findAllByGroups_NameIn(generalGroups));
//        convert general group identifiers. e.g. 12K2 to 12K
        Set<String> superiorGroups = generalGroups.stream().map(g -> {
            if (Character.isDigit(g.charAt(g.length() - 1)))
                return g.substring(0, g.length() - 1);
            return g;
        }).collect(Collectors.toSet());
//        check if subgroups are provided
        if(subgroups != null && !subgroups.isEmpty()){
//            check if superior group identifies the groups unambiguously
            if(superiorGroups.size() != 1)
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
        Set<String> generalGroupsGromRepository;
        Set<String> generalGroups = examDto.getGeneralGroups();
        Set<String> subgroups = examDto.getSubgroups();
//        if timetable service is unavailable verify general groups using GroupRepository
        try {
            generalGroupsGromRepository = new HashSet<>(timetableService.getGeneralGroupList());
        } catch (WebPageContentNotAvailableException e) {
            generalGroupsGromRepository = verifyUsingRepository(generalGroups);
        }
//        verify generalGroups using timetable service
        if (!generalGroupsGromRepository.containsAll(generalGroups)) {
            generalGroups.removeAll(generalGroupsGromRepository);
            throw new InvalidGroupIdentifierException(generalGroups);
        }
//        if there are no subgroups save exam for exercise groups or whole year e.g.
//               12K2             - exercise group exam
//               12K1, 12K2, 12K3 - whole year exam
        if (subgroups == null || subgroups.isEmpty()) {
//                TODO: change groups.name in database to unique
            return saveNewStudentGroups(generalGroups);
//         exams for subgroups e.g. L04 must have only superior group to avoid ambiguity
        } else if (generalGroups.size() == 1) {
//            if there are only one group change it from Set<String> to String
            String superiorGroup = generalGroups.iterator().next();
            Set<String> subGroupsFromTimetable;
            try {
                subGroupsFromTimetable = new HashSet<>(timetableService.getAvailableSubGroups(superiorGroup));
            } catch (JsonProcessingException |
                     SpecifiedGeneralGroupDoesntExistsException |
                     WebPageContentNotAvailableException e) {
                throw new ServiceNotAvailableException("Couldn't verify groups using timetable service");
//                TODO: add verification with repository when timetable service is unavailable
            }
//              verify if subgroups for specific general group exists
            if (!subGroupsFromTimetable.containsAll(subgroups)) {
                subgroups.removeAll(subGroupsFromTimetable);
                throw new InvalidGroupIdentifierException(subgroups);
            }
//              change superior group format e.g. 12K2 to 12K
            if (Character.isDigit(superiorGroup.charAt(superiorGroup.length() - 1)))
                superiorGroup = superiorGroup.substring(0, superiorGroup.length() - 1);
//              save subgroups with superior group identifier
            subgroups.add(superiorGroup);
            return saveNewStudentGroups(subgroups);
        }
//          only one general group could be assigned to subgroups (when there are more than 1 general group and
//          more than 0 subgroups)
        else
            throw new InvalidGroupIdentifierException("ambiguous general group for subgroups");
    }

    /**
     * @param groups groups that would be verified using repository
     * @return set of groups (String) when verification succeeded
     * @throws WebPageContentNotAvailableException when verification not succeeded
     */
    private Set<String> verifyUsingRepository(Set<String> groups) throws WebPageContentNotAvailableException {
        Set<String> groupsFromRepository = groupRepository.findAllByNameIn(groups).stream()
                .map(StudentGroup::getName)
                .collect(Collectors.toSet()
                );
        if (groupsFromRepository.containsAll(groups))
            return groups;
        else
            throw new ServiceNotAvailableException("Couldn't verify groups using repository");
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
                        .build())
                .collect(Collectors.toList())
        );
        existingGroups.addAll(savedGroups);
        return existingGroups;
    }
}
