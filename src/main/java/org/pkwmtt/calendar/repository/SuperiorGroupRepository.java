package org.pkwmtt.calendar.repository;

import org.pkwmtt.calendar.entity.SuperiorGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperiorGroupRepository extends JpaRepository<SuperiorGroup, Integer> {
    Optional<SuperiorGroup> findByName(String name);
}