package org.pkwmtt.calendar.events.services;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.calendar.events.dto.EventDTO;
import org.pkwmtt.calendar.events.mappers.EventsMapper;
import org.pkwmtt.calendar.events.repositories.EventsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventsService {
    final EventsRepository eventsRepository;
    
    public List<EventDTO> getAllEvents () {
        return eventsRepository.findAll()
          .stream()
          .map(EventsMapper::mapEventToEventDTO)
          .toList();
    }
    
    public List<EventDTO> getEventsForSuperiorGroup (String superiorGroupName) {
        return eventsRepository.findAll()
          .stream()
          .filter(item -> item.getSuperiorGroups()
            .stream()
            .anyMatch(group -> group.getName().equalsIgnoreCase(superiorGroupName)))
          .map(EventsMapper::mapEventToEventDTO)
          .toList();
    }
    
    public int addEvent (EventDTO eventDTO) {
        var event = EventsMapper.mapEventDTOToEvent(eventDTO);
        eventsRepository.save(event);
        return event.getId();
    }
}
