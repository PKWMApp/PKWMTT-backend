package org.pkwmtt.examCalendar.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.pkwmtt.exceptions.InvalidGroupIdentifierException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamDtoToExamMapperTest {

    @Mock
    private ExamTypeRepository examTypeRepository;

    @InjectMocks
    private ExamDtoToExamMapper examDtoToExamMapper;

    private ExamDto examDto;
    private String examTypeName;

//    @BeforeEach
//    void setup() {
//
//    }

    /**********************************************************************************/
//    mapToNewExam
    @Test
    void isFieldsMappedProperlyToNewExam() {
//        given
        String examTypeName = "exam";
        ExamDto examDto = new ExamDto(
                "Math exam",
                "Linear algebra",
                LocalDateTime.now().plusDays(1),
                "12K2, 13S1",
                examTypeName
        );
        when(examTypeRepository.findByName(examTypeName)).thenReturn(
                Optional.of(ExamType.builder()
                        .name(examTypeName)
                        .build())
        );
//        when
        Exam exam = examDtoToExamMapper.mapToNewExam(examDto);
//        then
//        test fields
        assertEquals(examDto.getTitle(), exam.getTitle());
        assertEquals(examDto.getDescription(), exam.getDescription());
        assertEquals(examDto.getDate(), exam.getDate());
        assertEquals(examDto.getExamGroups(), exam.getExamGroups());
        assertEquals(examTypeName, exam.getExamType().getName());
//        test null id
        assertNull(exam.getExamId());
    }

    @Test
    void ShouldThrowExceptionWhenGroupIdentifierIsLongerThanSixCharactersForNewExam() {
        //        given
        String examTypeName = "exam";
        ExamDto examDto = new ExamDto(
                "Math exam",
                "Linear algebra",
                LocalDateTime.now().plusDays(1),
                "12K2, 13S1, Not_Valid_Identifier, 41K1",
                examTypeName
        );
        when(examTypeRepository.findByName(examTypeName)).thenReturn(
                Optional.of(ExamType.builder()
                        .name(examTypeName)
                        .build())
        );
//        then
        RuntimeException exception = assertThrows(
                InvalidGroupIdentifierException.class,
                () -> examDtoToExamMapper.mapToNewExam(examDto)
        );
        assertEquals("Invalid group identifier: Not_Valid_Identifier", exception.getMessage());
    }


    /**********************************************************************************/
//    mapToExistingExam
    @Test
    void isFieldsMappedProperlyToExistingExam() {
        //        given
        int examId = 1;
        examTypeName = "exam";
        examDto = new ExamDto(
                "Math exam",
                "Linear algebra",
                LocalDateTime.now().plusDays(1),
                "12K2, 13S1",
                examTypeName
        );
        when(examTypeRepository.findByName(examTypeName)).thenReturn(
                Optional.of(ExamType.builder()
                        .name(examTypeName)
                        .build())
        );
//        when
        Exam exam = examDtoToExamMapper.mapToExistingExam(examDto, examId);
//        then
//        test fields
        assertEquals(examId, exam.getExamId());
        assertEquals(examDto.getTitle(), exam.getTitle());
        assertEquals(examDto.getDescription(), exam.getDescription());
        assertEquals(examDto.getDate(), exam.getDate());
        assertEquals(examDto.getExamGroups(), exam.getExamGroups());
        assertEquals(examTypeName, exam.getExamType().getName());
//        test not null id
        assertNotNull(exam.getExamId());
    }

    @Test
    void ShouldThrowExceptionWhenGroupIdentifierIsLongerThanSixCharactersForExistingExam() {
        //        given
        int examId = 1;
        String examTypeName = "exam";
        ExamDto examDto = new ExamDto(
                "Math exam",
                "Linear algebra",
                LocalDateTime.now().plusDays(1),
                "12K2, 13S1, Not_Valid_Identifier, 41K1",
                examTypeName
        );
        when(examTypeRepository.findByName(examTypeName)).thenReturn(
                Optional.of(ExamType.builder()
                        .name(examTypeName)
                        .build())
        );
//        then
        RuntimeException exception = assertThrows(
                InvalidGroupIdentifierException.class,
                () -> examDtoToExamMapper.mapToExistingExam(examDto, examId)
        );
        assertEquals("Invalid group identifier: Not_Valid_Identifier", exception.getMessage());
    }
}
