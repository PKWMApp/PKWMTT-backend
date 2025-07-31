package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
}