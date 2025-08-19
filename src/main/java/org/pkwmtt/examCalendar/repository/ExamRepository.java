package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

//    /**
//     * fetch all data  using one query
//     * @param group1 group identifier
//     * @param group2 group identifier
//     * @param group3 group identifier
//     * @param group4 group identifier
//     * @return set of Exams for specific groups
//     */
//    @Query("SELECT e FROM Exam e JOIN FETCH e.examType WHERE " +
//            "e.examGroups LIKE CONCAT('%', :g1, '%') OR " +
//            "e.examGroups LIKE CONCAT('%', :g2, '%') OR " +
//            "e.examGroups LIKE CONCAT('%', :g3, '%') OR " +
//            "e.examGroups LIKE CONCAT('%', :g4, '%') ")
//    Set<Exam> findExamsByGroupsIdentifier(
//            @Param("g1") String group1,
//            @Param("g2") String group2,
//            @Param("g3") String group3,
//            @Param("g4") String group4
//    );
//
//    /**
//     * fetch all data using one query
//     * @param group1 group identifier
//     * @param group2 group identifier
//     * @param group3 group identifier
//     * @return set of Exams for specific groups
//     */
//    @Query("SELECT e FROM Exam e JOIN FETCH e.examType WHERE " +
//            "e.examGroups LIKE CONCAT('%', :g1, '%') OR " +
//            "e.examGroups LIKE CONCAT('%', :g2, '%') OR " +
//            "e.examGroups LIKE CONCAT('%', :g3, '%') ")
//    Set<Exam> findExamsByGroupsIdentifier(
//            @Param("g1") String group1,
//            @Param("g2") String group2,
//            @Param("g3") String group3
//    );
//
//    /**
//     * fetch all data using one query
//     * @param group1 group identifier
//     * @param group2 group identifier
//     * @return set of Exams for specific groups
//     */
//    @Query("SELECT e FROM Exam e JOIN FETCH e.examType WHERE " +
//            "e.examGroups LIKE CONCAT('%', :g1, '%') OR " +
//            "e.examGroups LIKE CONCAT('%', :g2, '%')" )
//    Set<Exam> findExamsByGroupsIdentifier(
//            @Param("g1") String group1,
//            @Param("g2") String group2
//    );
//
//    /**
//     * fetch all data using one query
//     * @param group group identifier
//     * @return set of Exams for specific group
//     */
//    @Query("SELECT e FROM Exam e JOIN FETCH e.examType WHERE " +
//            "e.examGroups LIKE CONCAT('%', :gg, '%')")
//    Set<Exam> findExamsByGroupsIdentifier(
//            @Param("gg") String group
//    );

}