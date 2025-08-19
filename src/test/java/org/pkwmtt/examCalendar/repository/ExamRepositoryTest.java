package org.pkwmtt.examCalendar.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@DataJpaTest
class ExamRepositoryTest {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamTypeRepository examTypeRepository;

//    TODO: change exam repository and test new version
//    private ExamType examType;
//
//    @BeforeEach
//    void setup(){
//        examType = ExamType.builder()
//                .name("exam")
//                .build();
//        examTypeRepository.save(examType);
//    }
//
//    /**
//     * test if method find specific count of exams when 1 or 0 group identifiers match
//     */
//    @Test
//    void testSingleIdentifierMatch() {
////        given
//        Exam exam1 = Exam.builder()
//                .title("Exam 1")
//                .description("Exam 1")
//                .examDate(LocalDateTime.now().plusDays(1))
//                .examGroups("12K2, K03")
//                .examType(examType)
//                .build();
//        examRepository.save(exam1);
//        Exam exam2 = Exam.builder()
//                .title("Exam 2")
//                .description("Exam 2")
//                .examDate(LocalDateTime.now().plusDays(1))
//                .examGroups("12K3, K03, S02")
//                .examType(examType)
//                .build();
//        examRepository.save(exam2);
//        Exam exam3 = Exam.builder()
//                .title("Exam 3")
//                .description("Exam 3")
//                .examDate(LocalDateTime.now().plusDays(1))
//                .examGroups("13K1, K05, L05")
//                .examType(examType)
//                .build();
//        examRepository.save(exam3);
//        Exam exam4 = Exam.builder()
//                .title("Exam 4")
//                .description("Exam 4")
//                .examDate(LocalDateTime.now().plusDays(1))
//                .examGroups("41K1, L04, P03, I01")
//                .examType(examType)
//                .build();
//        examRepository.save(exam4);
//        Exam exam5 = Exam.builder()
//                .title("Exam 5")
//                .description("Exam 5")
//                .examDate(LocalDateTime.now().plusDays(1))
//                .examGroups("11A1, G03, H01, P02")
//                .examType(examType)
//                .build();
//        examRepository.save(exam5);
//
//        String generalGroup = "12K2";
//        String kGroup = "K05";
//        String lGroup = "L04";
//        String pGroup = "P02";
//
////        when
//        Set<Exam> exams = examRepository.findExamsByGroupsIdentifier(generalGroup, kGroup, lGroup, pGroup);
//        List<String> examsTitles = exams.stream().map(Exam::getTitle).toList();
////        then
//        assertEquals(4, exams.size());
//        assertTrue(examsTitles.contains("Exam 1"));
//        assertTrue(examsTitles.contains("Exam 3"));
//        assertTrue(examsTitles.contains("Exam 4"));
//        assertTrue(examsTitles.contains("Exam 5"));
//    }
//
//    /**
//     * test if method don't duplicate exams when more than 1 identifier match
//     */
//    @Test
//    void testMultipleIdentifierMatch() {
////        given
//        Exam exam1 = Exam.builder()
//                .title("Exam 1")
//                .description("Exam 1")
//                .date(LocalDateTime.now().plusDays(1))
//                .examGroups("12K2, K01, L04, P03, I01")
//                .examType(examType)
//                .build();
//        examRepository.save(exam1);
//        Exam exam2 = Exam.builder()
//                .title("Exam 2")
//                .description("Exam 2")
//                .date(LocalDateTime.now().plusDays(1))
//                .examGroups("12K2, K05, L04, P02")
//                .examType(examType)
//                .build();
//        examRepository.save(exam2);
//        Exam exam3 = Exam.builder()
//                .title("Exam 3")
//                .description("Exam 3")
//                .date(LocalDateTime.now().plusDays(1))
//                .examGroups("12K2, K05, L04, P02, I05")
//                .examType(examType)
//                .build();
//        examRepository.save(exam3);
//
//        String generalGroup = "12K2";
//        String kGroup = "K05";
//        String lGroup = "L04";
//        String pGroup = "P02";
//
////        when
//        Set<Exam> exams = examRepository.findExamsByGroupsIdentifier(generalGroup, kGroup, lGroup, pGroup);
//        List<String> examsTitles = exams.stream().map(Exam::getTitle).toList();
//
////        then
//        assertEquals(3, exams.size());
//        assertTrue(examsTitles.contains("Exam 1"));
//        assertTrue(examsTitles.contains("Exam 2"));
//        assertTrue(examsTitles.contains("Exam 3"));
//    }
//
//    /**
//     * test if method return empty set identifiers don't match
//     */
//    @Test
//    void testNothingMatch() {
////        given
//        Exam exam1 = Exam.builder()
//                .title("Exam 1")
//                .description("Exam 1")
//                .date(LocalDateTime.now().plusDays(1))
//                .examGroups("12K2, K01,")
//                .examType(examType)
//                .build();
//        examRepository.save(exam1);
//        Exam exam2 = Exam.builder()
//                .title("Exam 2")
//                .description("Exam 2")
//                .date(LocalDateTime.now().plusDays(1))
//                .examGroups("12K3, L05")
//                .examType(examType)
//                .build();
//        examRepository.save(exam2);
//
//        String generalGroup = "14K3";
//        String kGroup = "K05";
//        String lGroup = "L02";
//        String pGroup = "P02";
//
////        when
//        Set<Exam> exams = examRepository.findExamsByGroupsIdentifier(generalGroup, kGroup, lGroup, pGroup);
//
////        then
//        assertTrue(exams.isEmpty());
//    }
}