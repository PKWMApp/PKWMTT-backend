package org.pkwmtt.calendar.events.repositories;

import org.pkwmtt.calendar.events.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;


@SuppressWarnings("unused")
public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
}
