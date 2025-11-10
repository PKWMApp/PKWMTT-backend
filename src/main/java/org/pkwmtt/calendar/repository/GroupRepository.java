package org.pkwmtt.calendar.repository;

import org.pkwmtt.calendar.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface GroupRepository extends JpaRepository<StudentGroup, Integer> {
    Set<StudentGroup> findAllByNameIn(Set<String> names);
}