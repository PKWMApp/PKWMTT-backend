package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

    Set<Exam> findAllByTitle(String title);

    /**
     * @param groups set of generalGroups
     * @return list of exams for generalGroups
     */
    Set<Exam> findAllByGroups_NameIn(Set<String> groups);

    /**
     * @param generalGroup superior group of subgroups e.g. 12K
     * @param subgroup exam groups
     * @return list of exams for subgroups
     */
    @Query("""
                SELECT DISTINCT e FROM Exam e
                JOIN e.groups g1
                JOIN FETCH e.groups g2
                WHERE (g1.name = :superior AND g2.name IN :sub)
                OR g2.name IN :general
            """)
    Set<Exam> findAllBySubgroupsOfSuperiorGroupAndGeneralGroup(
            @Param("superior") String superiorGroup,
            @Param("general") Set<String> generalGroup,
            @Param("sub") Set<String> subgroup);

    @Query(value = """
            SELECT exam_id FROM exams_groups
            INNER JOIN student_groups ON exams_groups.group_id = student_groups.group_id
            WHERE student_groups.name IN (:groups)
            GROUP BY exam_id
            HAVING COUNT(DISTINCT exams_groups.group_id) = :expectedSize
            """, nativeQuery = true)
    Set<Integer> findCommonExamIdsForGroups(@Param("groups") Set<String> groups, @Param("expectedSize") int expectedSize);
}