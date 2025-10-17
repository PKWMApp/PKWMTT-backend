package org.pkwmtt.examCalendar.repository;

import jakarta.transaction.Transactional;
import org.pkwmtt.examCalendar.entity.Representative;
import org.pkwmtt.examCalendar.entity.SuperiorGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RepresentativeRepository extends JpaRepository<Representative, Integer> {
    Optional<Representative> findByEmail (String email);
    
    Optional<Representative> findBySuperiorGroup (SuperiorGroup superiorGroup);
    
    @Modifying
    @Transactional
    void deleteRepresentativeByEmail (String email);
}

