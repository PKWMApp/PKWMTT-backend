package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamTypeRepository extends JpaRepository<ExamType, Integer> {
    Optional<ExamType> findByName(String name);
}