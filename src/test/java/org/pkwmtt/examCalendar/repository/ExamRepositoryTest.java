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

    @BeforeAll
    void setUp() {
        ExamType examType = ExamType.builder()
                .name("exam").build();
        examTypeRepository.save(examType);

        StudentGroup g12A = StudentGroup.builder()
                .name("12A").build();
        StudentGroup g12A1 = StudentGroup.builder()
                .name("12A1").build();
        StudentGroup g12A2 = StudentGroup.builder()
                .name("12A2").build();

        StudentGroup g12K = StudentGroup.builder()
                .name("12K").build();
        StudentGroup g12K1 = StudentGroup.builder()
                .name("12K1").build();
        StudentGroup g12K2 = StudentGroup.builder()
                .name("12K2").build();
        StudentGroup g12K3 = StudentGroup.builder()
                .name("12K3").build();
        StudentGroup gL04 = StudentGroup.builder()
                .name("L04").build();
        StudentGroup gL05 = StudentGroup.builder()
                .name("L05").build();

        groupRepository.save(g12A);
        groupRepository.save(g12A1);
        groupRepository.save(g12A2);

        groupRepository.save(g12K);
        groupRepository.save(g12K1);
        groupRepository.save(g12K2);
        groupRepository.save(g12K3);
        groupRepository.save(gL04);
        groupRepository.save(gL05);

        Exam smallGroupExam1 = Exam.builder()
                .title("small Group Exam 1")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(g12K, gL04))
                .build();

        Exam smallGroupExam2 = Exam.builder()
                .title("small Group Exam 2")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(gL04, g12K, gL05))
                .build();

        Exam smallGroupExam3 = Exam.builder()
                .title("small Group Exam 3")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(g12A, gL05))
                .build();

        Exam generalGroupExam1 = Exam.builder()
                .title("general Group Exam 1")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(g12K1, g12K2, g12K3))
                .build();

        Exam generalGroupExam2 = Exam.builder()
                .title("general Group Exam 2")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(g12K1))
                .build();

        Exam generalGroupExam3 = Exam.builder()
                .title("general Group Exam 3")
                .description("Linear Algebra")
                .examDate(LocalDateTime.now().plusDays(1))
                .examType(examType)
                .groups(Set.of(g12A1, g12A2))
                .build();

        examRepository.save(smallGroupExam1);
        examRepository.save(smallGroupExam2);
        examRepository.save(smallGroupExam3);
        examRepository.save(generalGroupExam1);
        examRepository.save(generalGroupExam2);
        examRepository.save(generalGroupExam3);
    }

    @Test
    void shouldReturnExamsWhenNotAllSubgroupsFromRepositoryMatched() {
        List<Exam> exams = examRepository.findAllBySubgroupsOfGeneralGroup("12K", Set.of("L04"));
        assertEquals(2, exams.size());
        List<String> examTitles = exams.stream().map(Exam::getTitle).sorted().toList();
        assertEquals("small Group Exam 1", examTitles.get(0));
        assertEquals("small Group Exam 2", examTitles.get(1));
    }

    @Test
    void shouldReturnExamWhenNotAllSubgroupsFromArgumentsMatchedAndNotReturnExamsForWrongGeneralGroup() {
        List<Exam> exams = examRepository.findAllBySubgroupsOfGeneralGroup("12K", Set.of("L05"));
        assertEquals(1, exams.size());
        List<String> examTitles = exams.stream().map(Exam::getTitle).sorted().toList();
        assertEquals("small Group Exam 2", examTitles.getFirst());
    }

    @Test
    void shouldReturnExamsWhenMultipleArgumentsMatch() {
        List<Exam> exams = examRepository.findAllBySubgroupsOfGeneralGroup("12K", Set.of("L04", "L05"));
        assertEquals(2, exams.size());
        List<String> examTitles = exams.stream().map(Exam::getTitle).sorted().toList();
        assertEquals("small Group Exam 1", examTitles.get(0));
        assertEquals("small Group Exam 2", examTitles.get(1));
    }

    @Test
    void ShouldReturnEmptyListWhenSubgroupsSetIsEmpty() {
        List<Exam> exams = examRepository.findAllBySubgroupsOfGeneralGroup("12K", Set.of());
        assertTrue(exams.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenGeneralGroupIdentifierHasInvalidFormat() {
        List<Exam> exams = examRepository.findAllBySubgroupsOfGeneralGroup("12K2", Set.of("L04"));
        assertTrue(exams.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenGeneralGroupIdentifierDontMatch() {
        List<Exam> exams = examRepository.findAllBySubgroupsOfGeneralGroup("12B", Set.of("L04", "L05"));
        assertTrue(exams.isEmpty());
    }
}