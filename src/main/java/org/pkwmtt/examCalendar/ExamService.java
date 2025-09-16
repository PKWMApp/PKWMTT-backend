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
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;
    private final TimetableService timetableService;

    /**
     * @param examDto details of exam
     * @return id of exam added to database
     */
    public int addExam(ExamDto examDto) {

        verifyGroupPermissions(examDto.getGeneralGroups());

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

    /**
     * @param generalGroups set of general groups from the same year of study
     *                      e.g. 12K1, 12K2 are from 12K year of study,
     *                      but 12A1 and 12B1 or 11A1 and 12A1 aren't from the same year
     * @param subgroups subgroups that belong to provided general groups
     * @return set of exams containing provided groups
     */
    public Set<Exam> getExamByGroups(Set<String> generalGroups, Set<String> subgroups) {

        String superiorGroup = trimLastDigit(generalGroups);
        verifyGeneralGroupsFormat(generalGroups);

        if(subgroups == null || subgroups.isEmpty())
            return examRepository.findAllByGroups_NameIn(generalGroups);

        verifySubgroupsFormat(subgroups);
        return examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup(superiorGroup, generalGroups, subgroups);
    }

    /**
     * @return list of examTypes
     */
    public List<ExamType> getExamTypes() {
        return examTypeRepository.findAll();
    }


    /**
     * verify if groups exists and updates database when it exists, but repository doesn't contain it.
     * When timetable service is unavailable verifies groups using groupsRepository
     * @param examDto containing groups for verification
     * @return single set of all kinds of provided groups as StudentGroup entities
     * that are in database and could be safely attach to Exam entity
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

    /**
     * verifies provided generalGroups using timetable service or repository when service is unavailable
     * @param generalGroups that would be verified
     */
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
     * @param groups that would be verified using repository
     * @throws ServiceNotAvailableException when verification not succeeded
     */
    private void verifyGeneralGroupsUsingRepository(Set<String> groups) throws ServiceNotAvailableException {
        verifyGeneralGroupsFormat(groups);
        Set<String> groupsFromRepository = groupRepository.findAllByNameIn(groups).stream()
                .map(StudentGroup::getName)
                .collect(Collectors.toSet()
                );
        if (!groupsFromRepository.containsAll(groups))
            throw new ServiceNotAvailableException("Timetable service unavailable, couldn't verify groups using repository");
    }

    /**
     * verifies provided subgroups using timetable service or repository when service is unavailable
     * @param superiorGroup of provided subgroups
     * @param subgroups that would be verified
     */
    private void verifySubgroups(String superiorGroup, Set<String> subgroups){
        try {
            Set<String> subGroupsFromTimetable = new HashSet<>(timetableService.getAvailableSubGroups(superiorGroup));
            if (!subGroupsFromTimetable.containsAll(subgroups))
                throw new InvalidGroupIdentifierException(subGroupsFromTimetable, subgroups);
        } catch (JsonProcessingException |
                 SpecifiedGeneralGroupDoesntExistsException |
                 WebPageContentNotAvailableException e) {
            verifySubgroupsUsingRepository(superiorGroup,  subgroups);
        }
    }

    /**
     * @param superiorGroup of provided subgroups
     * @param groups subgroups for verification
     * @throws ServiceNotAvailableException when verification not succeeded
     */
    private void verifySubgroupsUsingRepository(String superiorGroup, Set<String> groups) throws ServiceNotAvailableException {
        groups.add(trimLastDigit(superiorGroup));
        if(examRepository.findCommonExamIdsForGroups(groups, groups.size()).isEmpty())
            throw new ServiceNotAvailableException("Timetable service unavailable, couldn't verify groups using repository");
    }

    /**
     * extract superior group form general group e.g. 12K2 -> 12K
     * @param generalGroup group for transformation
     * @return superior group
     */
    private static String trimLastDigit(String generalGroup) {
        char lastChar = generalGroup.charAt(generalGroup.length() - 1);
        if (Character.isDigit(lastChar))
            generalGroup = generalGroup.substring(0, generalGroup.length() - 1);
        return generalGroup;
    }

    /**
     * extract common superior group form provided general groups e.g. 12K2 -> 12K
     * @param superiorGroups set of general groups from the same year of study
     * @return single superior group of provided general groups
     * @throws InvalidGroupIdentifierException when not all provided groups belong to the same year of study
     */
    private static String trimLastDigit(Set<String> superiorGroups) throws InvalidGroupIdentifierException {
        Set<String> trimmedGroups = superiorGroups.stream()
                .map(ExamService::trimLastDigit)
                .collect(Collectors.toSet());
        if(trimmedGroups.size() > 1)
            throw new InvalidGroupIdentifierException("ambiguous general groups for subgroups");
        return trimmedGroups.iterator().next();
    }

    /**
     * saves groups to groupRepository, existing group names are filtered out before saving
     * @param groups groups that would be saved to repository
     * @return set of StudentsGroup Entities with provided names
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

    /**
     * @param generalGroups general groups for verification
     * @throws SpecifiedGeneralGroupDoesntExistsException when format is invalid
     */
    private static void verifyGeneralGroupsFormat(Set<String> generalGroups) throws SpecifiedGeneralGroupDoesntExistsException {
        generalGroups.forEach(group -> {
            if (!group.matches("^\\d.*"))
                throw new SpecifiedGeneralGroupDoesntExistsException(group);
        });
    }

    /**
     * @param subgroups subgroups for verification
     * @throws SpecifiedSubGroupDoesntExistsException when format is invalid
     */
    private static void verifySubgroupsFormat(Set<String> subgroups) throws SpecifiedSubGroupDoesntExistsException {
        subgroups.forEach(group -> {
            if (!group.matches("^[A-Z].*"))
                throw new SpecifiedSubGroupDoesntExistsException(group);
        });
    }

    /**
     * verifies if user had authorities to perform action for specific groups
     * @param groups set of provided groups
     */
    private void verifyGroupPermissions(Set<String> groups){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String) authentication.getPrincipal();
        String userGroup = userRepository.findGroupByUserEmail(userEmail).orElseThrow(() -> new AccessDeniedException("There are no group assigned to user"));
        if(!trimLastDigit(groups).equals(userGroup))
            throw new AccessDeniedException("You don't have permission to access this group");
    }
}