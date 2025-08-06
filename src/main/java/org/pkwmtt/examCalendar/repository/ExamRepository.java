package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    @Query("select e from Exam e where e.examGroups LIKE %:gs%")
    Set<Exam> findExamsByGroupSignature(@Param("gs")String groupSignature);

    @Query("SELECT e FROM Exam e WHERE " +
            "e.examGroups LIKE CONCAT('%', :g1, '%') OR " +
            "e.examGroups LIKE CONCAT('%', :g2, '%') OR " +
            "e.examGroups LIKE CONCAT('%', :g3, '%') OR " +
            "e.examGroups LIKE CONCAT('%', :g4, '%') ")
    Set<Exam> findExamsByGroupsIdentifier(
            @Param("g1") String group1,
            @Param("g2") String group2,
            @Param("g3") String group3,
            @Param("g4") String group4
    );

    @Query("SELECT e FROM Exam e WHERE " +
            "e.examGroups LIKE CONCAT('%', :g1, '%') OR " +
            "e.examGroups LIKE CONCAT('%', :g2, '%') OR " +
            "e.examGroups LIKE CONCAT('%', :g3, '%') ")
    Set<Exam> findExamsByGroupsIdentifier(
            @Param("g1") String group1,
            @Param("g2") String group2,
            @Param("g3") String group3
    );

    @Query("SELECT e FROM Exam e WHERE " +
            "e.examGroups LIKE CONCAT('%', :g1, '%') OR " +
            "e.examGroups LIKE CONCAT('%', :g2, '%')" )
    Set<Exam> findExamsByGroupsIdentifier(
            @Param("g1") String group1,
            @Param("g2") String group2
    );

    @Query("SELECT e FROM Exam e WHERE " +
            "e.examGroups LIKE CONCAT('%', :gg, '%')")
    Set<Exam> findExamsByGroupsIdentifier(
            @Param("gg") String group
    );

    /**
     * @param groupSignature symbol that identifies group
     * @return set of Exams for specific group
     */
    Set<Exam> findByExamGroupsContaining(String groupSignature);
}