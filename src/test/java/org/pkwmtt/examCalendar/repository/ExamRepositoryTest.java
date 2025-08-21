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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Integer exam1Id;
    private Integer exam2Id;
    private Integer exam3Id;
    private Integer exam4Id;

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

        Exam exam1 = Exam.builder()
                .title("math exam")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(gL04))
                .build();

        Exam exam2 = Exam.builder()
                .title("math exam")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(g12K1, g12K2, gL04, gL05))
                .build();

        Exam exam3 = Exam.builder()
                .title("math exam")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(g12K1))
                .build();

        Exam exam4 = Exam.builder()
                .title("math exam")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(gL04, g12K1))
                .build();

        exam1Id = examRepository.save(exam1).getExamId();
        exam2Id = examRepository.save(exam2).getExamId();
        exam3Id = examRepository.save(exam3).getExamId();
        exam4Id = examRepository.save(exam4).getExamId();
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
        Set<Integer> examsId = exams.stream().map(Exam::getExamId).collect(Collectors.toSet());

//        then

        assertEquals(1, exams.size());
        assertTrue(examsId.contains(exam2Id));

    }

    @Test
    void shouldReturnFourElementsOutOfFour() {
//        when
        Set<Exam> exams = examRepository.findByGroupsIn(Set.of(g12K1, gL04));
        Set<Integer> examsId = exams.stream().map(Exam::getExamId).collect(Collectors.toSet());
//        then
        assertEquals(4, exams.size());
        assertTrue(examsId.contains(exam1Id));
        assertTrue(examsId.contains(exam2Id));
        assertTrue(examsId.contains(exam3Id));
        assertTrue(examsId.contains(exam4Id));
    }

    @Test
    void ShouldReturnEmptySetWhenNoGroups() {
//        when
        Set<Exam> exams = examRepository.findByGroupsIn(Set.of());
//        then
        assertTrue(exams.isEmpty());
    }

    @Test
    void ShouldReturnEmptySetWhenGroupNotExistsInDatabase() {
//        when
        Set<Exam> exams = examRepository.findByGroupsIn(Set.of(
                StudentGroup.builder()
                        .groupId(Integer.MAX_VALUE)
                        .name("NotValid").build())
        );
//        then
        assertTrue(exams.isEmpty());
    }


}