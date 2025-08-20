package org.pkwmtt.examCalendar.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.pkwmtt.examCalendar.repository.GroupRepository;
import org.pkwmtt.exceptions.InvalidGroupIdentifierException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamDtoToExamMapperTest {

    @Mock
    private ExamTypeRepository examTypeRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private ExamDtoToExamMapper examDtoToExamMapper;

    private ExamDto examDto;
    private String examTypeName;

    private Set<StudentGroup> groups;

    @BeforeEach
    void setup(){
        StudentGroup group = StudentGroup.builder()
                .name("12K2")
                .build();
        groups = new HashSet<>();
        groups.add(group);
    }

    /**********************************************************************************/
//    mapToNewExam
    @Test
    void isFieldsMappedProperlyToNewExam() {
//        given
        String examTypeName = "exam";
        String groupIdentifier = "12K2";
        ExamDto examDto = new ExamDto(
                "Math exam",
                "Linear algebra",
                LocalDateTime.now().plusDays(1),
                examTypeName,
                Set.of("12K2")
        );
        when(examTypeRepository.findByName(examTypeName)).thenReturn(
                Optional.of(ExamType.builder()
                        .name(examTypeName)
                        .build())
        );

        when(groupRepository.findAllByNameIn(Set.of(groupIdentifier))).thenReturn(
                new HashSet<>(groups)
        );
//        when
        Exam exam = examDtoToExamMapper.mapToNewExam(examDto);
//        then
//        test fields
        assertEquals(examDto.getTitle(), exam.getTitle());
        assertEquals(examDto.getDescription(), exam.getDescription());
        assertEquals(examDto.getDate(), exam.getExamDate());
        assertEquals(examDto.getExamGroups(), exam.getGroups().stream().map(StudentGroup::getName).collect(Collectors.toSet()));
        assertEquals(examTypeName, exam.getExamType().getName());
//        test null id
        assertNull(exam.getExamId());
    }

//    TODO: change to checking if exam group exists
    @Test
    @Disabled("new version required")
    void ShouldThrowExceptionWhenGroupIdentifierIsLongerThanSixCharactersForNewExam() {
        //        given
//        StudentGroup group = StudentGroup.builder()
//                .name("Not_Valid_Identifier")
//                .build();
//        groups.add(group);
        String examTypeName = "exam";
        ExamDto examDto = new ExamDto(
                "Math exam",
                "Linear algebra",
                LocalDateTime.now().plusDays(1),
                examTypeName,
                Set.of("Not_Valid_Identifier")
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
        String groupIdentifier = "12K2";
        examDto = new ExamDto(
                "Math exam",
                "Linear algebra",
                LocalDateTime.now().plusDays(1),
                examTypeName,
                Set.of(groupIdentifier)
        );
        when(examTypeRepository.findByName(examTypeName)).thenReturn(
                Optional.of(ExamType.builder()
                        .name(examTypeName)
                        .build())
        );

        when(groupRepository.findAllByNameIn(Set.of(groupIdentifier))).thenReturn(
                new HashSet<>(groups)
        );
//        when
        Exam exam = examDtoToExamMapper.mapToExistingExam(examDto, examId);
//        then
//        test fields
        assertEquals(examId, exam.getExamId());
        assertEquals(examDto.getTitle(), exam.getTitle());
        assertEquals(examDto.getDescription(), exam.getDescription());
        assertEquals(examDto.getDate(), exam.getExamDate());
        assertEquals(examDto.getExamGroups(), exam.getGroups().stream().map(StudentGroup::getName).collect(Collectors.toSet()));
        assertEquals(examTypeName, exam.getExamType().getName());
//        test not null id
        assertNotNull(exam.getExamId());
    }

    @Test
    @Disabled("new version required")
    void ShouldThrowExceptionWhenGroupIdentifierIsLongerThanSixCharactersForExistingExam() {
        //        given
        int examId = 1;
        StudentGroup group = StudentGroup.builder()
                .name("Not_Valid_Identifier")
                .build();
        groups.add(group);
        String examTypeName = "exam";
        ExamDto examDto = new ExamDto(
                "Math exam",
                "Linear algebra",
                LocalDateTime.now().plusDays(1),
                examTypeName,
                Set.of("12K2")
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
