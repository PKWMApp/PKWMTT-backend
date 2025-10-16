package org.pkwmtt.examCalendar.repository;

import jakarta.transaction.Transactional;
import org.pkwmtt.examCalendar.entity.SuperiorGroup;
import org.pkwmtt.examCalendar.entity.Representative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RepresentativeRepository extends JpaRepository<Representative, Integer> {
    Optional<Representative> findByEmail(String email);
    Optional<Representative> findBySuperiorGroup(SuperiorGroup superiorGroup);

    @Query("SELECT g.name FROM Representative r LEFT JOIN r.superiorGroup g where r.email = :email")
    @Transactional
    void deleteRepresentativeByEmail(String email);
}

