package org.pkwmtt.calendar.events.repositories;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.pkwmtt.calendar.events.entities.EventType;

@SuppressWarnings("unused")
public interface EventTypeRepository extends JpaAttributeConverter<EventType, Integer> {
}
