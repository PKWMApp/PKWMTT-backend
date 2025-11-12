package org.pkwmtt.calendar.events.services;

import org.pkwmtt.calendar.events.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
}