package org.pkwmtt.calendar.events.controllers;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.calendar.events.dto.EventDTO;
import org.pkwmtt.calendar.events.services.EventsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${apiPrefix}/events")
public class EventsController {
    final EventsService service;
    
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents (@RequestParam(required = false, name = "g") String superiorGroup) {
        if (superiorGroup != null) {
            return ResponseEntity.ok().body(service.getEventsForSuperiorGroup(superiorGroup));
        }
        return ResponseEntity.ok().body(service.getEventsForSuperiorGroup(null));
    }
}
