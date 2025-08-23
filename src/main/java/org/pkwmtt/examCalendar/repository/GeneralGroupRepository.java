package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.GeneralGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneralGroupRepository extends JpaRepository<GeneralGroup, Integer> {
    Optional<GeneralGroup> findByName (String name);
}