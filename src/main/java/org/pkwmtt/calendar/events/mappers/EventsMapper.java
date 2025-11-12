package org.pkwmtt.calendar.events.mappers;

import org.pkwmtt.calendar.enities.SuperiorGroup;
import org.pkwmtt.calendar.events.dto.EventDTO;
import org.pkwmtt.calendar.events.entities.Event;
import org.pkwmtt.calendar.events.entities.EventType;

import java.util.List;

/**
 * Utility mapper for converting between Event entities, EventType entities and EventDTOs.
 * <p>
 * All methods are static and stateless; this class provides a centralized place for
 * transformation logic used when exchanging data between persistence/entities and DTOs.
 */
public class EventsMapper {
    
    /**
     * Map an {@link Event} entity to an {@link EventDTO}.
     * <p>
     * The mapping includes:
     * - title
     * - description
     * - type name
     * - start and end dates
     * - superior group names (converted from {@link SuperiorGroup})
     * <p>
     * Note: this method assumes that required nested properties (like {@code event.getType()}
     * and {@code event.getSuperiorGroups()}) are present; callers should handle potential
     * {@code null} values if needed.
     *
     * @param event the source Event entity to map
     * @return a populated EventDTO representing the given Event
     */
    public static EventDTO mapEventToEventDTO (Event event) {
        return new EventDTO()
          .setTitle(event.getTitle())
          .setDescription(event.getDescription())
          .setType(event.getType().getName())
          .setStartDate(event.getStartDate())
          .setEndDate(event.getEndDate())
          .setSuperiorGroups(event.getSuperiorGroups().stream().map(SuperiorGroup::getName).toList());
        
    }
    
    /**
     * Map an {@link EventDTO} to an {@link Event} entity.
     * <p>
     * Only the basic fields required by the Event constructor are set:
     * - title
     * - description
     * - startDate
     * - endDate
     * <p>
     * Additional relationships (type, superior groups, ids, etc.) are not handled here
     * and should be resolved by the caller or service layer.
     *
     * @param eventDTO the source DTO containing event data
     * @return a new Event entity populated from the DTO
     */
    public static Event mapEventDTOToEvent (EventDTO eventDTO) {
        return new Event(
          eventDTO.getTitle(),
          eventDTO.getDescription(),
          eventDTO.getStartDate(),
          eventDTO.getEndDate()
        );
    }
    
    /**
     * Convert a list of {@link EventType} entities to a list of their names.
     * <p>
     * Example usage: converting persisted event types to a simple list of strings for DTOs
     * or UI consumption.
     *
     * @param eventTypes list of EventType entities to convert
     * @return list of names extracted from the provided eventTypes
     */
    public static List<String> mapEventTypeListToListOfString (List<EventType> eventTypes) {
        return eventTypes.stream().map(EventType::getName).toList();
    }
}