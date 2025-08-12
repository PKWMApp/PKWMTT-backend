package org.pkwmtt.examCalendar;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.mapper.ExamDtoToExamMapper;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * integration tests of ExamCalendar
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@RequiredArgsConstructor
class ExamControllerTest {

    @Autowired
    private final ExamTypeRepository examTypeRepository;
    @Autowired
    private final ExamRepository examRepository;
    @Autowired
    private final ExamDtoToExamMapper examDtoToExamMapper;
    @Autowired
    private final ExamService examService;

    @BeforeEach
    void setup(){
        examRepository.deleteAll();
    }

    @Test
    void addExam() {
//        Exam exam = Exam.builder().build();
    }

    @Test
    void modifyExam() {
    }

    @Test
    void deleteExam() {
    }

    @Test
    void getExam() {
    }

    @Test
    void getExams() {
    }

    @Test
    void getExamTypes() {
    }
}