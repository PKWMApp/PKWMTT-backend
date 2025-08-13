package org.pkwmtt.repository;

import org.pkwmtt.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<StudentGroup, Integer> {
}