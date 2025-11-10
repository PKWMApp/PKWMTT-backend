package org.pkwmtt.calendar.events.mappers;

import org.pkwmtt.calendar.events.dto.EventDTO;
import org.pkwmtt.calendar.events.entities.Event;

public class EventsMapper {
    public static EventDTO mapEventToEventDTO (Event event) {
        return new EventDTO()
          .setTitle(event.getTitle())
          .setDescription(event.getDescription())
          .setStartDate(event.getStartDate())
          .setEndDate(event.getEndDate());
    }
}
