package org.pkwmtt.studentCodes.repository;

import jakarta.transaction.Transactional;
import org.pkwmtt.examCalendar.entity.SuperiorGroup;
import org.pkwmtt.examCalendar.entity.StudentCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentCodeRepository extends JpaRepository<StudentCode, Integer> {
    Optional<StudentCode> findByCode(String code);

    @Transactional
    void deleteByCode(String code);

    boolean existsBySuperiorGroup(SuperiorGroup superiorGroup);

    boolean existsByCode(String code);

    @Transactional
    void deleteBySuperiorGroup(SuperiorGroup superiorGroup);
    
}