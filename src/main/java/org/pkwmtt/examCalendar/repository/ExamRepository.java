package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    @Query("select e from Exam e where e.examGroups LIKE %:gs%")
    Set<Exam> findExamsByGroupSignature(@Param("gs")String groupSignature);
    Set<Exam> findByExamGroupsContaining(String groupSignature);
}