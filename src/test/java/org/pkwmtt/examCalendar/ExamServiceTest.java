package org.pkwmtt.examCalendar;

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
import org.pkwmtt.examCalendar.mapper.ExamDtoToExamMapper;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.pkwmtt.exceptions.UnsupportedCountOfArgumentsException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private ExamDtoToExamMapper examDtoToExamMapper;

    @InjectMocks
    private ExamService examService;

    @Test
    void addExam() {
//        given
        int examId = 1;
        Set<StudentGroup> examGroups = new HashSet<>();
        examGroups.add(StudentGroup.builder().name("12K2").build());
        examGroups.add(StudentGroup.builder().name("13L1").build());
        ExamDto examDto = new ExamDto(
                "Math exam",
                "desc",
                LocalDateTime.now().plusDays(1),
                "Exam",
                examGroups
        );
        Exam exam = Exam.builder()
                .title("Math exam")
                .description("desc")
                .examDate(LocalDateTime.now().plusDays(1))
                .groups(examGroups)
                .examType(new ExamType(1, "Exam"))
                .build();
        when(examDtoToExamMapper.mapToNewExam(examDto)).thenReturn(exam);

//        assign exam id in repository
        when(examRepository.save(exam)).thenAnswer(invocation -> {
            Exam newExam = invocation.getArgument(0, Exam.class);
            Field field = Exam.class.getDeclaredField("examId");
            field.setAccessible(true);
            field.set(newExam, examId);
            return newExam;
        });
//        when
        int result = examService.addExam(examDto);
//        then
        assertEquals(examId, result);
        verify(examRepository).save(exam);
        verify(examDtoToExamMapper).mapToNewExam(examDto);
    }

    /************************************************************************************/
//modify exam
    @Test
    void shouldModifyExamWhenIdExists() {
        //        given
        int examId = 1;
        ExamDto examDto = mock(ExamDto.class);
        Exam exam = mock(Exam.class);

        when(examDtoToExamMapper.mapToExistingExam(examDto, examId)).thenReturn(exam);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
//        when
        examService.modifyExam(examDto, examId);
//        then
        verify(examDtoToExamMapper).mapToExistingExam(examDto, examId);
        verify(examRepository).save(exam);
    }

    @Test
    void shouldThrowWhenExamIdNotExists() {
        //        given
        int examId = 5;
        ExamDto examDto = mock(ExamDto.class);
        when(examRepository.findById(examId)).thenThrow(new NoSuchElementException("Exam not found"));
//        when
        RuntimeException exception = assertThrows(
                NoSuchElementException.class,
                () -> examService.modifyExam(examDto, examId)
        );
//        then
        verify(examDtoToExamMapper, never()).mapToExistingExam(examDto, examId);
        verify(examRepository, never()).save(any());
        assertEquals("Exam not found", exception.getMessage());
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
//        assertEquals(4, passedGroups.size());
//    }

}