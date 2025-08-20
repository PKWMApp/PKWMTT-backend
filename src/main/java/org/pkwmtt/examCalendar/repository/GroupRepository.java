package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface GroupRepository extends JpaRepository<StudentGroup, Integer> {
    Set<StudentGroup> findAllByNameIn(Set<String> names);
}