package org.pkwmtt.examCalendar;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.pkwmtt.examCalendar.repository.GroupRepository;
import org.pkwmtt.exceptions.InvalidGroupIdentifierException;
import org.pkwmtt.exceptions.ServiceNotAvailableException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.TimetableService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ExamTypeRepository examTypeRepository;

    @Mock
    private TimetableService timetableService;

    @InjectMocks
    private ExamService examService;

    //<editor-fold desc="repository don't contain groups, service available">

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - blank
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void testBlankSubgroupAndMoreArgumentsThatRequiredReturnedByService() {
//        given
        Set<String> g12K2 = Set.of("12K2");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = ExamDto.builder()
                .title("title")
                .description("description")
                .date(date)
                .examType("exam")
                .generalGroups(new HashSet<>(g12K2))
                .build();
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(g12K2);
        Exam exam = buildExamWithIdAndGroups(1, studentGroups);

        when(examTypeRepository.findByName(examDto.getExamType())).thenReturn(Optional.of(examType));
//        more groups than in set
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K1", "12K2", "12K3")));
        when(groupRepository.findAllByNameIn(g12K2)).thenReturn(new HashSet<>(Set.of()));
        when(groupRepository.saveAll(anyList())).thenReturn(studentGroups);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
//        when
        int savedId = examService.addExam(examDto);
//        then
        verify(examTypeRepository, times(1)).findByName(examDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(g12K2);

        ArgumentCaptor<List<StudentGroup>> groupCaptor = ArgumentCaptor.forClass(List.class);
        verify(groupRepository, times(1)).saveAll(groupCaptor.capture());
        assertEquals("12K2", groupCaptor.getValue().getFirst().getName());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, g12K2);
    }

    /**
     * test specification
     * generalGroup         - 3 item
     * subgroup             - 0 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void addExamForMultipleGeneralGroupsWithEmptySubgroups() {
        //        given
        Set<String> generalGroups = Set.of("12K1", "12K2", "12K3");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(generalGroups);
        Exam exam = buildExamWithIdAndGroups(1, studentGroups);

        when(examTypeRepository.findByName(examDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));

        when(groupRepository.findAllByNameIn(generalGroups)).thenReturn(new HashSet<>(Set.of()));
        when(groupRepository.saveAll(anyList())).thenReturn(studentGroups);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
//        when
        int savedId = examService.addExam(examDto);
//        then
        verify(examTypeRepository, times(1)).findByName(examDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(generalGroups);

        ArgumentCaptor<List<StudentGroup>> groupCaptor = ArgumentCaptor.forClass(List.class);
        verify(groupRepository, times(1)).saveAll(groupCaptor.capture());
        Set<String> capturedGroups = groupCaptor.getValue().stream().map(StudentGroup::getName).collect(Collectors.toSet());
        assertEquals(generalGroups, capturedGroups);

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, generalGroups);
    }


    /**
     * test specification
     * generalGroup         - 3 item
     * subgroup             - 2 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void shouldThrowWhenThereAreMoreThan1GeneralGroupsAndSubgroupsIsPresent() {
        //        given
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        Set<String> generalGroups = Set.of("12K1", "12K2", "12K3");
        Set<String> subgroups = Set.of("L04", "L05");
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));
        RuntimeException exception = assertThrows(InvalidGroupIdentifierException.class, () -> examService.addExam(examDto));
        assertEquals("Invalid group identifier: ambiguous general groups for subgroups",exception.getMessage());
    }

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 1 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void addExamForSingleGeneralGroupAndSingleSubgroup() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04");
        when(timetableService.getAvailableSubGroups(any(String.class))).thenReturn(new ArrayList<>(List.of("K03", "K04", "L04")));
        testExamServiceForSubgroups(generalGroups, subgroups);
    }

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 4 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void addExamForSingleGeneralGroupAndMultipleSubgroup() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04", "P04", "L04", "L03");
        when(timetableService.getAvailableSubGroups(any(String.class))).thenReturn(new ArrayList<>(List.of("K03", "K04", "P04", "L04", "L03")));
        testExamServiceForSubgroups(generalGroups, subgroups);
    }


    /**
     * test specification
     * generalGroup         - 0 item
     * subgroup             - 1 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void addExamForEmptyGeneralGroup() {
        //        given
        Set<String> generalGroups = Set.of();
        Set<String> subgroups = Set.of("K04");
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        RuntimeException exception = assertThrows(InvalidGroupIdentifierException.class, () -> examService.addExam(examDto));
        assertEquals("Invalid group identifier: general group is missing" ,exception.getMessage());
    }

    //</editor-fold>

//    TODO: test blank generalGroups in controller

    //<editor-fold desc="service available, groups don't match service">
    /**
     * test specification
     * generalGroup         - 2 item
     * subgroup             - 0 items
     * timetable service    - available
     * provided groups      - don't match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void shouldThrowWhenGeneralGroupsDontMatchService() {
        //        given
        Set<String> generalGroups = Set.of("12K1", "12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of()));
//        when
        RuntimeException exception = assertThrows(InvalidGroupIdentifierException.class, () -> examService.addExam(examDto));
//        then
        assertEquals("Invalid group identifiers: [12K1, 12K2]", exception.getMessage());
    }

    @Test
    void shouldThrowWhenNotAllGeneralGroupsMatchService() {
        //        given
        Set<String> generalGroups = Set.of("12K1", "12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K1")));
//        when
        RuntimeException exception = assertThrows(InvalidGroupIdentifierException.class, () -> examService.addExam(examDto));
//        then
        assertEquals("Invalid group identifiers: [12K2]", exception.getMessage());
    }

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 3 items
     * timetable service    - available
     * provided groups      - partially match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void shouldThrowWhenSubgroupsDontMatchService() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04", "P04", "L04");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K2")));
        when(timetableService.getAvailableSubGroups("12K2")).thenReturn(List.of("K05"));
//        when
        RuntimeException exception = assertThrows(InvalidGroupIdentifierException.class, () -> examService.addExam(examDto));
//        then
        String message = exception.getMessage();
        assertTrue(message.startsWith("Invalid group identifiers:"));
        assertFalse(message.contains("12K2"));
        assertTrue(message.contains("K04"));
        assertTrue(message.contains("P04"));
        assertTrue(message.contains("L04"));
        assertFalse(message.contains("K05"));
    }

    @Test
    void shouldThrowWhenNotAllSubgroupsMatchService() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04", "P04", "L04");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K2")));
        when(timetableService.getAvailableSubGroups("12K2")).thenReturn(List.of("P04", "L04", "K05"));
//        when
        RuntimeException exception = assertThrows(InvalidGroupIdentifierException.class, () -> examService.addExam(examDto));
//        then
        String message = exception.getMessage();
        assertTrue(message.startsWith("Invalid group identifiers:"));
        assertFalse(message.contains("12K2"));
        assertTrue(message.contains("K04"));
        assertFalse(message.contains("P04"));
        assertFalse(message.contains("L04"));
        assertFalse(message.contains("K05"));
    }

    //</editor-fold>

    //<editor-fold desc="repository contain groups, service available">
    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 0 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - contain provided groups
     */
    @Test
    void addExamForSingleGeneralGroupWithRepositoryContainingGroup() {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(generalGroups);
        Exam exam = buildExamWithIdAndGroups(1, studentGroups);

        when(examTypeRepository.findByName(examDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));

        when(groupRepository.findAllByNameIn(generalGroups)).thenReturn(new HashSet<>(studentGroups));
        when(groupRepository.saveAll(any())).thenReturn(List.of());
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
//        when
        int savedId = examService.addExam(examDto);
//        then
        verify(examTypeRepository, times(1)).findByName(examDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(any());       //???
        verify(groupRepository, times(1)).saveAll(List.of());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, generalGroups);
    }

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 4 items
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - partially contain provided groups
     */
    @Test
    void addExamForSingleGeneralGroupAndSubgroupsWithRepositoryContainingGroups() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("K04", "P04", "L04", "K05");
        Set<String> combinedGroups = Set.of("12K", "K04", "P04", "L04", "K05");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(combinedGroups);
        Exam exam = buildExamWithIdAndGroups(1, studentGroups);

        when(examTypeRepository.findByName(examDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));
        when(timetableService.getAvailableSubGroups("12K2")).thenReturn(List.of("K04", "P04", "L04", "K05"));

        when(groupRepository.findAllByNameIn(any(Set.class))).thenReturn(new HashSet<>(studentGroups.subList(0,3)));
        when(groupRepository.saveAll(any())).thenReturn(studentGroups.subList(3,5));
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
//        when
        int savedId = examService.addExam(examDto);
//        then
        verify(examTypeRepository, times(1)).findByName(examDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(any());
        verify(groupRepository, times(1)).saveAll(any());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, combinedGroups);
    }

    //</editor-fold>

    //<editor-fold desc="repository don't contain groups, service unavailable">
    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 0 item
     * timetable service    - unavailable
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void unavailableServiceAndRepositoryDontMatch() {
//        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);

//        more groups than in set
        when(timetableService.getGeneralGroupList()).thenThrow(new WebPageContentNotAvailableException());
        when(groupRepository.findAllByNameIn(generalGroups)).thenReturn(new HashSet<>(Set.of()));
//        when
        RuntimeException exception = assertThrows(ServiceNotAvailableException.class, () -> examService.addExam(examDto));
//        then
        assertEquals("Couldn't verify groups using repository" ,exception.getMessage());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(generalGroups);
    }


    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 3 items
     * timetable service    - unavailable
     * provided groups      - match groups from timetable service
     * groupRepository      - partially contain provided groups
     */
    @Test
    void unavailableServiceAndRepositoryDontMatchForSubgroups() throws JsonProcessingException {
//        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("L04", "K04", "P04");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(Set.of("12K2", "L04"));

//        more groups than in set
        when(timetableService.getGeneralGroupList()).thenThrow(new WebPageContentNotAvailableException());
        when(timetableService.getAvailableSubGroups("12K2")).thenThrow(new WebPageContentNotAvailableException());
        when(groupRepository.findAllByNameIn(any())).thenReturn(new HashSet<>(studentGroups));
//        when
        RuntimeException exception = assertThrows(ServiceNotAvailableException.class, () -> examService.addExam(examDto));
//        then
        assertEquals("Couldn't verify groups using timetable service" ,exception.getMessage());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(generalGroups);
    }

    //</editor-fold>

    //<editor-fold desc="repository contain groups, service unavailable">
    /**
     * test specification
     * generalGroup         - 2 item
     * subgroup             - 0 item
     * timetable service    - unavailable
     * provided groups      - match groups from timetable service
     * groupRepository      - contain provided groups
     */
    @Test
    void addExamWhenServiceIsUnavailableAndRepositoryContainsGeneralGroups(){
        //        given
        Set<String> generalGroups = Set.of("12K1", "12K2");
        Set<String> subgroups = Set.of();

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(generalGroups);
        Exam exam = buildExamWithIdAndGroups(1, studentGroups);

        when(examTypeRepository.findByName(examDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenThrow(new WebPageContentNotAvailableException());

        when(groupRepository.findAllByNameIn(generalGroups)).thenReturn(new HashSet<>(studentGroups));
        when(groupRepository.saveAll(anyList())).thenReturn(studentGroups);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
//        when
        int savedId = examService.addExam(examDto);
//        then
        verify(examTypeRepository, times(1)).findByName(examDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(2)).findAllByNameIn(any());
        verify(groupRepository, times(1)).saveAll(any());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, generalGroups);
    }

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - 4 items
     * timetable service    - unavailable
     * provided groups      - match groups from timetable service
     * groupRepository      - contain provided groups
     */
    @Test
    @Disabled("Not supported yet")
    void addExamWhenServiceIsUnavailableAndRepositoryContainsGroups() throws JsonProcessingException {
        //        given
        Set<String> generalGroups = Set.of("12K2");
        Set<String> subgroups = Set.of("L04", "K04", "P04", "K05");
        Set<String> combinedGroups = Set.of("12K2", "L04", "K04", "P04", "K05");

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(combinedGroups);
        Exam exam = buildExamWithIdAndGroups(1, studentGroups);

        when(examTypeRepository.findByName(examDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenThrow(new WebPageContentNotAvailableException());
        when(timetableService.getAvailableSubGroups("12K2")).thenThrow(new JsonParseException("parsing subgroups failed"));

        when(groupRepository.findAllByNameIn(any(Set.class))).thenReturn(new HashSet<>(studentGroups));
        when(groupRepository.saveAll(anyList())).thenReturn(List.of());
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
//        when
        int savedId = examService.addExam(examDto);
//        then
        verify(examTypeRepository, times(1)).findByName(examDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(2)).findAllByNameIn(any());
        verify(groupRepository, times(1)).saveAll(any());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, generalGroups);
    }

    //</editor-fold>

    /************************************************************************************/
//modify exam
    @Test
    void shouldModifyExamWhenIdExists() {

    }

    @Test
    void shouldThrowWhenExamIdNotExists() {
        //        given

    }

    /************************************************************************************/
//delete exam
    @Test
    void shouldDeleteExamWhenIdExists() {
//        given
        int examId = 1;
        when(examRepository.findById(examId)).thenReturn(Optional.of(mock(Exam.class)));
//        when
        examService.deleteExam(examId);
//        then
        verify(examRepository).deleteById(examId);
    }

    @Test
    void shouldThrowExceptionWhenExamIdNotExists() {
//        given
        int examId = 5;
        when(examRepository.findById(examId)).thenThrow(new NoSuchElementException("Exam not found"));
//        when
        RuntimeException exception = assertThrows(
                NoSuchElementException.class,
                () -> examService.deleteExam(examId)
        );
//        then
        verify(examRepository, never()).deleteById(examId);
        assertEquals("Exam not found", exception.getMessage());
    }

    /************************************************************************************/
//    getExamById
    @Test
    void getExamById() {
//        given
        int examId = 1;
        when(examRepository.findById(examId)).thenReturn(Optional.of(mock(Exam.class)));
//        when
        Exam exam = examService.getExamById(examId);
//        then
        verify(examRepository).findById(examId);
        assertNotNull(exam);
    }

    @Test
    void shouldThrowExceptionWhenExamNotFound() {
//        given
        int examId = 5;
        when(examRepository.findById(examId)).thenThrow(new NoSuchElementException("Exam not found"));
//        when
        RuntimeException exception = assertThrows(
                NoSuchElementException.class,
                () -> examService.getExamById(examId)
        );
//        then
        assertEquals("Exam not found", exception.getMessage());
    }

    //    getExamByGroup
    //    FIXME: write test for new version of this method
//    @Test
//    void shouldThrowWithMoreThan4Arguments() {
////        given
//        Set<String> groups = new HashSet<>();
//        groups.add("12K2");
//        groups.add("13L1");
//        groups.add("13A2");
//        groups.add("41S2");
//        groups.add("11S3");
////        when
//        RuntimeException exception = assertThrows(
//                UnsupportedCountOfArgumentsException.class,
//                () -> examService.getExamByGroup(groups)
//        );
////        then
//        assertEquals(
//                "Invalid count of arguments provided: 5 expected more than: 1 less than: 5",
//                exception.getMessage()
//        );
//    }


//    @Test
//    void shouldCallRepositoryWith4Arguments() {
////        given
//        Set<String> groups = new HashSet<>();
//        groups.add("12K2");
//        groups.add("13L1");
//        groups.add("13A2");
//        groups.add("41S2");
//        Exam mockExam = mock(Exam.class);
//        Set<Exam> exams = new HashSet<>();
//        exams.add(mockExam);
//        when(examRepository.findExamsByGroupsIdentifier(any(), any(), any(), any())).thenReturn(exams);
////        when
//        Set<Exam> result = examService.getExamByGroup(groups);
////        then
//        List<ArgumentCaptor<String>> cap = new ArrayList<>();
//        for (int i = 0; i < 4; ++i)
//            cap.add(ArgumentCaptor.forClass(String.class));
//
//        verify(examRepository).findExamsByGroupsIdentifier(
//                cap.get(0).capture(),
//                cap.get(1).capture(),
//                cap.get(2).capture(),
//                cap.get(3).capture()
//        );
//        Set<String> passedGroups = cap.stream().map(ArgumentCaptor::getValue).collect(Collectors.toSet());
//
//        assertEquals(groups, passedGroups);
//        assertEquals(exams, result);
//    }
//
//
//    @Test
//    void shouldCallRepositoryWith3Arguments() {
////        given
//        Set<String> groups = new HashSet<>();
//        groups.add("12K2");
//        groups.add("13L1");
//        groups.add("13A2");
//        Exam mockExam = mock(Exam.class);
//        Set<Exam> exams = new HashSet<>();
//        exams.add(mockExam);
//        when(examRepository.findExamsByGroupsIdentifier(any(), any(), any())).thenReturn(exams);
////        when
//        Set<Exam> result = examService.getExamByGroup(groups);
////        then
//        List<ArgumentCaptor<String>> cap = new ArrayList<>();
//        for (int i = 0; i < 3; ++i)
//            cap.add(ArgumentCaptor.forClass(String.class));
//
//        verify(examRepository).findExamsByGroupsIdentifier(
//                cap.get(0).capture(),
//                cap.get(1).capture(),
//                cap.get(2).capture()
//        );
//        Set<String> passedGroups = cap.stream().map(ArgumentCaptor::getValue).collect(Collectors.toSet());
//
//        assertEquals(groups, passedGroups);
//        assertEquals(exams, result);
//    }
//
//    @Test
//    void shouldCallRepositoryWith2Arguments() {
////        given
//        Set<String> groups = new HashSet<>();
//        groups.add("12K2");
//        groups.add("13L1");
//        Exam mockExam = mock(Exam.class);
//        Set<Exam> exams = new HashSet<>();
//        exams.add(mockExam);
//        when(examRepository.findExamsByGroupsIdentifier(any(), any())).thenReturn(exams);
////        when
//        Set<Exam> result = examService.getExamByGroup(groups);
////        then
//        List<ArgumentCaptor<String>> cap = new ArrayList<>();
//        for (int i = 0; i < 2; ++i)
//            cap.add(ArgumentCaptor.forClass(String.class));
//
//        verify(examRepository).findExamsByGroupsIdentifier(
//                cap.get(0).capture(),
//                cap.get(1).capture()
//        );
//        Set<String> passedGroups = cap.stream().map(ArgumentCaptor::getValue).collect(Collectors.toSet());
//
//        assertEquals(groups, passedGroups);
//        assertEquals(exams, result);
//    }
//
//    @Test
//    void shouldCallRepositoryWithSingleArguments() {
////        given
//        Set<String> groups = new HashSet<>();
//        groups.add("12K2");
//        Exam mockExam = mock(Exam.class);
//        Set<Exam> exams = new HashSet<>();
//        exams.add(mockExam);
//        when(examRepository.findExamsByGroupsIdentifier(any())).thenReturn(exams);
////        when
//        Set<Exam> result = examService.getExamByGroup(groups);
////        then
//        ArgumentCaptor<String> cap = ArgumentCaptor.forClass(String.class);
//
//        verify(examRepository).findExamsByGroupsIdentifier(cap.capture());
//        Set<String> passedGroups = new HashSet<>();
//        passedGroups.add(cap.getValue());
//
//        assertEquals(groups, passedGroups);
//        assertEquals(exams, result);
//    }
//
//
//    @Test
//    void shouldCallRepositoryWithDuplicatesOf4UniqueArguments() {
////        given
//        Set<String> groups = new HashSet<>();
//        groups.add("12K2");
//        groups.add("13L1");
//        groups.add("13A2");
//        groups.add("41S2");
//        groups.add("41S2");
//        groups.add("13L1");
//        Exam mockExam = mock(Exam.class);
//        Set<Exam> exams = new HashSet<>();
//        exams.add(mockExam);
//        when(examRepository.findExamsByGroupsIdentifier(any(), any(), any(), any())).thenReturn(exams);
////        when
//        Set<Exam> result = examService.getExamByGroup(groups);

    /// /        then
//        List<ArgumentCaptor<String>> cap = new ArrayList<>();
//        for (int i = 0; i < 4; ++i)
//            cap.add(ArgumentCaptor.forClass(String.class));
//
//        verify(examRepository).findExamsByGroupsIdentifier(
//                cap.get(0).capture(),
//                cap.get(1).capture(),
//                cap.get(2).capture(),
//                cap.get(3).capture()
//        );
//        Set<String> passedGroups = cap.stream().map(ArgumentCaptor::getValue).collect(Collectors.toSet());
//
//        assertEquals(groups, passedGroups);
//        assertEquals(exams, result);
//        assertEquals(4, passedGroups.size());
//    }


//    helper methods
//    private ExamDto getExampleExamDto(String examTypeName) {
//        return ExamDto.builder()
//                .title("title")
//                .description("desc")
//                .date(LocalDateTime.now().plusDays(1))
//                .examType(examTypeName)
//                .examGroups(Set.of("12K1", "P05", "L02"))
//                .build();
//    }
//
//    private ExamType saveExampleExamType(String examTypeName) {
//        return ExamType.builder().examTypeId(1).name(examTypeName).build();
//    }
//
//    private List<String> mockGetGeneralGroupList(){
//        List<String> groups = List.of("12K1", "12K2", "12K3");
//        when(timetableService.getGeneralGroupList()).thenReturn(groups);
//        return groups;
//    }
//
//    private void mockGetSubGroupsList() throws JsonProcessingException {
//        List<String> groups1 = List.of(
//                "K01", "K04", "L01", "L02", "L04", "P01", "P04"
//        );
//        List<String> groups2 = List.of(
//                "K02", "K04", "K05", "L02", "L03", "L04", "L05", "P02", "P04", "P05"
//        );
//        List<String> groups3 = List.of(
//                "K03", "K05", "L03", "L05", "L06", "P03", "P05"
//        );
//
//        when(timetableService.getAvailableSubGroups("12K1")).thenReturn(groups1);
//        when(timetableService.getAvailableSubGroups("12K2")).thenReturn(groups2);
//        when(timetableService.getAvailableSubGroups("12K3")).thenReturn(groups3);
//        return new ArrayList<String>(groups1, groups2, groups3);
//    }
//
//    private void mockGroupRepositoryFindByName() {
//        when(groupRepository.findAllByNameIn(Set.of("12K1", "P05", "L02"))).thenReturn(
//                Stream.of("12K1", "P05", "L02").map(s ->
//                        StudentGroup.builder()
//                                .name(s)
//                                .build()
//                ).collect(Collectors.toSet())
//        );
//    }
//
//    private void mockExamRepositoryFindByName() {
//        when(examTypeRepository.findByName("exam")).thenReturn(Optional.of(ExamType.builder()
//                .examTypeId(1)
//                .name("exam")
//                .build()));
//    }
//
//    private Set<StudentGroup> getExampleStudentGroupsSet() {
//        return Stream.of("12K1", "P05", "L02").map(s ->
//                StudentGroup.builder()
//                        .name(s)
//                        .build()
//        ).collect(Collectors.toSet());
//    }
//
//    private void mockExamRepositorySaveExam(Set<StudentGroup> studentGroups) {
//        when(examRepository.save(any(Exam.class))).thenReturn(Exam.builder()
//                .examId(1)
//                .title("title")
//                .description("desc")
//                .examDate(LocalDateTime.now().plusDays(1))
//                .examType(ExamType.builder()
//                        .examTypeId(1)
//                        .name("exam")
//                        .build())
//                .groups(studentGroups)
//                .build()
//        );
//    }
    private static List<StudentGroup> buildExampleStudentGroupList(Set<String> groupNames) {
        AtomicInteger id = new AtomicInteger();
        return groupNames.stream()
                .map(g -> StudentGroup.builder()
                        .groupId(id.getAndIncrement())
                        .name(g)
                        .build()
                ).collect(Collectors.toList());
    }

    private static Exam buildExamWithIdAndGroups(int id, List<StudentGroup> groups) {
        return Exam.builder()
                .examId(id)
                .groups(new HashSet<>(groups))
                .build();
    }

    private static ExamType buildExampleExamType() {
        return ExamType.builder()
                .examTypeId(1)
                .name("exam")
                .build();
    }

    private static ExamDto buildExampleExamDto(Set<String> generalGroups, Set<String> subgroups, LocalDateTime date) {
        return ExamDto.builder()
                .title("title")
                .description("description")
                .date(date)
                .examType("exam")
                .generalGroups(new HashSet<>(generalGroups))
                .subgroups(new HashSet<>(subgroups))
                .build();
    }

    private static void assertExam(Exam savedExam, LocalDateTime date, int savedId, Set<String> groups) {
        assertEquals("title", savedExam.getTitle());
        assertEquals("description", savedExam.getDescription());
        assertEquals(date, savedExam.getExamDate());
        assertEquals("exam", savedExam.getExamType().getName());
        assertEquals(groups.size(), savedExam.getGroups().size());
        assertEquals(groups, savedExam.getGroups().stream().map(StudentGroup::getName).collect(Collectors.toSet()));
        assertEquals(1, savedId);
    }

    private void testExamServiceForSubgroups(Set<String> generalGroups, Set<String> subgroups) {
        Set<String> combinedGroups = new HashSet<>(subgroups);
        combinedGroups.addAll(generalGroups.stream()
                .map(g -> g.matches(".*\\d$") ? g.substring(0, g.length() - 1) : g)
                .collect(Collectors.toSet()));

        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = buildExampleExamDto(generalGroups, subgroups, date);
        ExamType examType = buildExampleExamType();
        List<StudentGroup> studentGroups = buildExampleStudentGroupList(combinedGroups);
        Exam exam = buildExamWithIdAndGroups(1, studentGroups);

        when(examTypeRepository.findByName(examDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(generalGroups));

        when(groupRepository.findAllByNameIn(combinedGroups)).thenReturn(new HashSet<>(Set.of()));
        when(groupRepository.saveAll(anyList())).thenReturn(studentGroups);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
//        when
        int savedId = examService.addExam(examDto);
//        then
        verify(examTypeRepository, times(1)).findByName(examDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(combinedGroups);

        ArgumentCaptor<List<StudentGroup>> groupCaptor = ArgumentCaptor.forClass(List.class);
        verify(groupRepository, times(1)).saveAll(groupCaptor.capture());
        Set<String> capturedGroups = groupCaptor.getValue().stream().map(StudentGroup::getName).collect(Collectors.toSet());
        assertEquals(combinedGroups, capturedGroups);

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertExam(savedExam, date, savedId, combinedGroups);
    }
}