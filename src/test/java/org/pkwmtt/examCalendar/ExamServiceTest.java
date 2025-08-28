package org.pkwmtt.examCalendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.pkwmtt.examCalendar.mapper.ExamDtoMapper;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.pkwmtt.examCalendar.repository.GroupRepository;
import org.pkwmtt.timetable.TimetableService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * test specification
     * generalGroup         - 1 item
     * subgroup             - blank
     * timetable service    - available
     * provided groups      - match groups from timetable service
     * groupRepository      - don't contain provided groups
     */
    @Test
    void addExamWithCorrectData() {
//        given
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        ExamDto examDto = ExamDto.builder()
                .title("title")
                .description("description")
                .date(date)
                .examType("exam")
                .generalGroups(new HashSet<>(Set.of("12K2")))
                .build();

        ExamType examType = ExamType.builder()
                .examTypeId(1)
                .name("exam")
                .build();

        Exam exam = Exam.builder()
                .examId(1)
                .groups(Set.of(StudentGroup.builder().groupId(1).name("12K2").build()))
                .build();

        StudentGroup groupWithId = StudentGroup.builder()
                .groupId(1)
                .name("12K2")
                .build();

        List<StudentGroup> groupListWithId = new ArrayList<>();
        groupListWithId.add(groupWithId);

        when(examTypeRepository.findByName(examDto.getExamType())).thenReturn(Optional.of(examType));
        when(timetableService.getGeneralGroupList()).thenReturn(new ArrayList<>(List.of("12K1", "12K2", "12K3")));
        when(groupRepository.findAllByNameIn(Set.of("12K2"))).thenReturn(new HashSet<>(Set.of()));
        when(groupRepository.saveAll(anyList())).thenReturn(groupListWithId);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
//        when
        int savedId = examService.addExam(examDto);
//        then
        verify(examTypeRepository, times(1)).findByName(examDto.getExamType());
        verify(timetableService, times(1)).getGeneralGroupList();
        verify(groupRepository, times(1)).findAllByNameIn(Set.of("12K2"));

        ArgumentCaptor<List<StudentGroup>> groupCaptor = ArgumentCaptor.forClass(List.class);
        verify(groupRepository, times(1)).saveAll(groupCaptor.capture());
        assertEquals("12K2", groupCaptor.getValue().getFirst().getName());

        ArgumentCaptor<Exam> examCaptor = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository, times(1)).save(examCaptor.capture());
        Exam savedExam = examCaptor.getValue();
        assertEquals("title", savedExam.getTitle());
        assertEquals("description", savedExam.getDescription());
        assertEquals(date, savedExam.getExamDate());
        assertEquals("exam", savedExam.getExamType().getName());
        assertEquals(1, savedExam.getGroups().size());
        assertEquals("12K2", savedExam.getGroups().iterator().next().getName());
        assertEquals(1, savedId);
    }

    @Test
    void addExamForMultipleGeneralGroupsWithEmptySubgroups() {
        ExamDto examDto = ExamDto.builder()
                .title("title")
                .description("description")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of())
                .build();
    }

    @Test
    void addExamForSingleSubgroup() {

    }

    @Test
    void addExamForMultipleSubgroups() {

    }

    @Test
    void addExamWithWrongExamType() {
    }

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


}