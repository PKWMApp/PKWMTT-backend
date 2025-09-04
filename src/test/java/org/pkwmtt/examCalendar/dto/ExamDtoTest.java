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

    @Test
    void shouldSuccessWithCompleteData() {
//        given
        ExamDto examDto = ExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
//        when, then
        assertTrue(validator.validate(examDto).isEmpty());
    }

    @Test
    void shouldSuccessWithEmptyDescription() {
//        given
        ExamDto examDto = ExamDto.builder()
                .title("Math exam")
                .description("")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
//        when, then
        assertTrue(validator.validate(examDto).isEmpty());
    }

    @Test
    void shouldSuccessWithBlankDescription() {
//        given
        ExamDto examDto = ExamDto.builder()
                .title("Math exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
//        when, then
        assertTrue(validator.validate(examDto).isEmpty());
    }

    @Test
    void shouldSuccessWithBlankSubgroups() {
//        given
        ExamDto examDto = ExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .build();
//        when, then
        assertTrue(validator.validate(examDto).isEmpty());
    }

    @Test
    void shouldSuccessWithEmptySubgroups() {
//        given
        ExamDto examDto = ExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of(""))
                .build();
//        when, then
        assertTrue(validator.validate(examDto).isEmpty());
    }


    //    empty Strings
    @Test
    void shouldFailWithEmptyTitle() {
        //        given
        ExamDto examDto = ExamDto.builder()
                .title("")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of(""))
                .build();
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldFailWithBlankTitle() {
        //        given
        ExamDto examDto = ExamDto.builder()
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of(""))
                .build();
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldFailWithEmptyGeneralGroups() {
        //        given
        ExamDto examDto = ExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of())
                .subgroups(Set.of("L04"))
                .build();
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("generalGroups")));
    }

    @Test
    void shouldFailWithBlankGeneralGroups() {
        //        given
        ExamDto examDto = ExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .subgroups(Set.of("L04"))
                .build();
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("generalGroups")));
    }

//    to long Strings

    @Test
    void ShouldFailWithTooLongTitle() {
        //        given
        ExamDto examDto = ExamDto.builder()
//                256 characters
                .title("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void toLongDescription() {
        //        given
        ExamDto examDto = ExamDto.builder()
//                256 characters
                .title("Math exam")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
//        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void dateNotInFuture() {
        //        given
        ExamDto examDto = ExamDto.builder()
//                256 characters
                .title("Math exam")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .date(LocalDateTime.now().minusHours(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
        //        when
        Set<ConstraintViolation<ExamDto>> violations = validator.validate(examDto);
//        then
        assertFalse(validator.validate(examDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("date")));
    }

}