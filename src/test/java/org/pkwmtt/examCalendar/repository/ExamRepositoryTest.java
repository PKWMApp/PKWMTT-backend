package org.pkwmtt.examCalendar.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("database")
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
                .groups(Set.of(g12K1, g12K2))
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
        Set<Exam> exams = examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup("12K", Set.of("12K3"), Set.of("L04"));
        assertEquals(2, exams.size());
        List<String> examTitles = exams.stream().map(Exam::getTitle).sorted().toList();
        assertEquals("small Group Exam 1", examTitles.get(0));
        assertEquals("small Group Exam 2", examTitles.get(1));
    }

    @Test
    void shouldReturnExamWhenNotAllSubgroupsFromArgumentsMatchedAndNotReturnExamsForWrongGeneralGroup() {
        Set<Exam> exams = examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup("12K", Set.of("12K3"), Set.of("L05"));
        assertEquals(1, exams.size());
        List<String> examTitles = exams.stream().map(Exam::getTitle).sorted().toList();
        assertEquals("small Group Exam 2", examTitles.getFirst());
    }

    @Test
    void shouldReturnExamsWhenMultipleArgumentsMatch() {
        Set<Exam> exams = examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup("12K", Set.of("12K3"), Set.of("L04", "L05"));
        assertEquals(2, exams.size());
        Set<String> examTitles = exams.stream().map(Exam::getTitle).collect(Collectors.toSet());
        assertTrue(examTitles.contains("small Group Exam 1"));
        assertTrue(examTitles.contains("small Group Exam 2"));
    }

    @Test
    void shouldReturnOnlyExamsForGeneralGroups() {
        Set<Exam> exams = examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup("12K", Set.of("12K1"), Set.of("L01", "L08"));
        assertEquals(2, exams.size());
        Set<String> examTitles = exams.stream().map(Exam::getTitle).collect(Collectors.toSet());
        assertTrue(examTitles.contains("general Group Exam 1"));
        assertTrue(examTitles.contains("general Group Exam 2"));
    }

    @Test
    void shouldReturnGeneralGroupExamsWhenSubgroupsIsEmpty() {
        Set<Exam> exams = examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup("12K", Set.of("12K1"), Set.of());
        assertEquals(2, exams.size());
        Set<String> examTitles = exams.stream().map(Exam::getTitle).collect(Collectors.toSet());
        assertTrue(examTitles.contains("general Group Exam 1"));
        assertTrue(examTitles.contains("general Group Exam 2"));
    }

    @Test
    void shouldReturnExamsForGeneralAndSubgroups() {
        Set<Exam> exams = examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup("12K", Set.of("12K2"), Set.of("L04", "L05"));
        assertEquals(3, exams.size());
        Set<String> examTitles = exams.stream().map(Exam::getTitle).collect(Collectors.toSet());
        assertTrue(examTitles.contains("small Group Exam 1"));
        assertTrue(examTitles.contains("small Group Exam 2"));
        assertTrue(examTitles.contains("general Group Exam 1"));
    }

    @Test
    void ShouldReturnEmptyListWhenSubgroupsSetIsEmpty() {
        Set<Exam> exams = examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup("12K", Set.of(), Set.of());
        assertTrue(exams.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenGeneralGroupIdentifierHasInvalidFormat() {
        Set<Exam> exams = examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup("12K2", Set.of(), Set.of("L04"));
        assertTrue(exams.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenGeneralGroupIdentifierDontMatch() {
        Set<Exam> exams = examRepository.findAllBySubgroupsOfSuperiorGroupAndGeneralGroup("12B", Set.of("12B1"), Set.of("L04", "L05"));
        assertTrue(exams.isEmpty());
    }
}