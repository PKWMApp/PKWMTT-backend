package org.pkwmtt.events.repository;

import org.pkwmtt.events.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}

