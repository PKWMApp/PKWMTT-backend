package org.pkwmtt.examCalendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.mapper.ExamDtoToExamMapper;
import org.pkwmtt.examCalendar.repository.ExamRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

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
        ExamDto examDto = new ExamDto(
                "Math exam",
                "desc",
                LocalDateTime.now().plusDays(1),
                "12K2, 13L1",
                "Exam"
        );
        Exam exam = Exam.builder()
                .title("Math exam")
                .description("desc")
                .date(LocalDateTime.now().plusDays(1))
                .examGroups("12K2, 13L1")
                .examType(new ExamType(1, "Exam"))
                .build();
        when(examDtoToExamMapper.mapToNewExam(examDto)).thenReturn(exam);

//        assign exam id in repository
        when(examRepository.save(exam)).thenAnswer(invocation -> {
            Exam newExam = invocation.getArgument(0);
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
//    get exam by id

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

    @Test
    void getExamByGroup() {

    }
}