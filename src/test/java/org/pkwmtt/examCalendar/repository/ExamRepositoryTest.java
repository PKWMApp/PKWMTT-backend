package org.pkwmtt.examCalendar.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExamRepositoryTest {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamTypeRepository examTypeRepository;

    @Autowired
    private GroupRepository groupRepository;

    private StudentGroup g12K1;
    private StudentGroup g12K2;
    private StudentGroup g12K3;
    private StudentGroup gL04;
    private StudentGroup gL05;

    private Exam exam1;
    private Exam exam2;
    private Exam exam3;
    private Exam exam4;

    @BeforeAll
    void setUp() {
        ExamType examType = ExamType.builder()
                .name("exam").build();
        examTypeRepository.save(examType);

        g12K1 = StudentGroup.builder()
                .name("12K1").build();
        g12K2 = StudentGroup.builder()
                .name("12K2").build();
        g12K3 = StudentGroup.builder()
                .name("12K3").build();
        gL04 = StudentGroup.builder()
                .name("L04").build();
        gL05 = StudentGroup.builder()
                .name("L05").build();

        groupRepository.save(g12K1);
        groupRepository.save(g12K2);
        groupRepository.save(g12K3);
        groupRepository.save(gL04);
        groupRepository.save(gL05);

        exam1 = Exam.builder()
                .title("math exam")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(gL04))
                .build();

        exam2 = Exam.builder()
                .title("math exam")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(g12K1, g12K2 ,gL04, gL05))
                .build();

        exam3 = Exam.builder()
                .title("math exam")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(g12K1))
                .build();

        exam4 = Exam.builder()
                .title("math exam")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(gL04, g12K1))
                .build();

        examRepository.save(exam1);
        examRepository.save(exam2);
        examRepository.save(exam3);
        examRepository.save(exam4);
    }


    @Test
    void shouldReturnEmptySet() {
//        when
        Set<Exam> exams = examRepository.findByGroupsIn(Set.of(g12K3));
//        then
        assertTrue(exams.isEmpty());
    }

    @Test
    void shouldReturnOneElementOutOfFour() {
//        when
        Set<Exam> exams = examRepository.findByGroupsIn(Set.of(g12K2, gL05));
//        then
        assertEquals(1, exams.size());
        assertTrue(exams.contains(exam2));

    }

    @Test
    void shouldReturnFourElementsOutOfFour() {
//        when
        Set<Exam> exams = examRepository.findByGroupsIn(Set.of(g12K1, gL04));
//        then
        assertEquals(4, exams.size());
        assertTrue(exams.contains(exam1));
        assertTrue(exams.contains(exam2));
        assertTrue(exams.contains(exam3));
        assertTrue(exams.contains(exam4));
    }

    @Test
    void ShouldReturnEmptySetWhenNoGroups() {
//        when
        Set<Exam> exams = examRepository.findByGroupsIn(Set.of());
//        then
        assertTrue(exams.isEmpty());
    }

//    FIXME:
    @Test
    void ShouldReturnEmptySetWhenGroupNotExistsInDatabase() {
//        when
        Set<Exam> exams = examRepository.findByGroupsIn(Set.of(
                StudentGroup.builder()
                .name("NotValid").build())
        );
//        then
        assertTrue(exams.isEmpty());
    }


}