package org.pkwmtt.examCalendar.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.pkwmtt.examCalendar.entity.StudentGroup;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExamDtoTest {

    private final Validator validator;

    public ExamDtoTest() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Mock
    private ExamDto examDto;

    private Set<StudentGroup> groups;

    @BeforeEach
    void setup(){
        StudentGroup group = StudentGroup.builder()
                .name("12K2")
                .build();
        groups = new HashSet<>();
        groups.add(group);
    }

    @Test
    void validData() {
//        given
        ExamDto examDto = new ExamDto(
                "Math exam",
                "First exam",
                LocalDateTime.now().plusDays(1),
                "exam",
                groups
        );
//        when, then
        assertTrue(validator.validate(examDto).isEmpty());
    }


    //    empty Strings
    @Test
    void emptyStringTitle() {
        //        given
        ExamDto examDto = new ExamDto(
                "",
                "First exam",
                LocalDateTime.now().plusDays(1),
                "exam",
                groups
        );
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void emptyExamGroups() {
        //        given
        //        clear groups set
        groups.clear();
        ExamDto examDto = new ExamDto(
                "Math exam",
                "First exam",
                LocalDateTime.now().plusDays(1),
                "exam",
                groups
        );
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("examGroups")));
    }

//    to long Strings

    @Test
    void toLongStringTitle() {
        //        given
        ExamDto examDto = new ExamDto(
//              256 characters
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "First exam",
                LocalDateTime.now().plusDays(1),
                "exam",
                groups
        );
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void toLongDescription() {
        //        given
        ExamDto examDto = new ExamDto(
                "Math exam",
//                256 characters
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                LocalDateTime.now().plusDays(1),
                "exam",
                groups
        );
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    //    TODO: change to too large set
//    @Test
//    void toLongExamGroups() {
//        //        given
//        ExamDto examDto = new ExamDto(
//                "Math exam",
//                "First exam",
//                LocalDateTime.now().plusDays(1),
//                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
//                "exam"
//        );
////        when
//        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
////        then
//        assertFalse(validator.validate(examDto).isEmpty());
//        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("examGroups")));
//    }

//    date not in future

    @Test
    void dateNotInFuture() {
        //        given
        ExamDto examDto = new ExamDto(
                "Math exam",
                "First exam",
                LocalDateTime.now().minusHours(1),
                "exam",
                groups
        );
        //        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("date")));
    }

}