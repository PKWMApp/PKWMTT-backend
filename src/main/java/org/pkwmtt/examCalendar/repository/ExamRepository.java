package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

//    @Query("SELECT e FROM Exam e JOIN FETCH e.examType JOIN FETCH e.groups g WHERE g IN :gr")
//    Set<Exam> findByGroupsIn(@Param("gr") Set<StudentGroup> groups);

    /**
     * @param groups set of generalGroups
     * @return list of exams for generalGroups
     */
    List<Exam> findAllByGroups_NameIn(Set<String> groups);

    /**
     * @param generalGroup superior group of subgroups e.g. 12K
     * @param subgroup exam groups
     * @return list of exams for subgroups
     */
    @Query("""
                SELECT DISTINCT e FROM Exam e
                JOIN e.groups g1
                JOIN FETCH e.groups g2
                WHERE g1.name = :general AND g2.name IN :sub
            """)
    List<Exam> findAllBySubgroupsOfGeneralGroup(@Param("general") String generalGroup , @Param("sub") Set<String> subgroup);




}