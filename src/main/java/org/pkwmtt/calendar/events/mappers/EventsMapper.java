package org.pkwmtt.calendar.events.mappers;

import org.pkwmtt.calendar.enities.SuperiorGroup;
import org.pkwmtt.calendar.events.dto.EventDTO;
import org.pkwmtt.calendar.events.entities.Event;
import org.pkwmtt.calendar.events.entities.EventType;

import java.util.List;

public class EventsMapper {
    
    public static EventDTO mapEventToEventDTO (Event event) {
        return new EventDTO()
          .setTitle(event.getTitle())
          .setDescription(event.getDescription())
          .setType(event.getType().getName())
          .setStartDate(event.getStartDate())
          .setEndDate(event.getEndDate())
          .setSuperiorGroups(event.getSuperiorGroups().stream().map(SuperiorGroup::getName).toList());
        
    }
    
    public static Event mapEventDTOToEvent (EventDTO eventDTO) {
        return new Event(
          eventDTO.getTitle(),
          eventDTO.getDescription(),
          eventDTO.getStartDate(),
          eventDTO.getEndDate()
        );
    }
    
    public static List<String> mapEventTypeListToListOfString (List<EventType> eventTypes) {
        return eventTypes.stream().map(EventType::getName).toList();
    }
}
