package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

    @Query("SELECT e FROM Exam e JOIN FETCH e.examType JOIN FETCH e.groups g WHERE g IN :gr")
    Set<Exam> findByGroupsIn(@Param("gr") Set<StudentGroup> groups);


}