package org.pkwmtt.examCalendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.examCalendar.repository.ExamRepository;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @InjectMocks
    private ExamService examService;

    @Test
    void addExam() {
    }

    @Test
    void modifyExam() {
    }

    @Test
    void deleteExam() {
    }

    @Test
    void getExamById() {
    }

    @Test
    void getExamByGroup() {
    }
}