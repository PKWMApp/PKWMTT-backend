package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamTypeRepository extends JpaRepository<ExamType, Integer> {
}