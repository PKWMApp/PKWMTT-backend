package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

    Set<Exam> findAllByTitle(String title);

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
    Set<Exam> findAllBySubgroupsOfGeneralGroup(@Param("general") String generalGroup, @Param("sub") Set<String> subgroup);
}