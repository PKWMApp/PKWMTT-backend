package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<StudentGroup, Integer> {
}