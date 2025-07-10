package org.pkwmtt.timetable;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/pkmwtt/api/v1/timetable")
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService service;

    @GetMapping("/{generalGroupName}")
    public ResponseEntity<TimetableDTO> getGeneralGroupScheduleFiltered(
        @PathVariable String generalGroupName,
        @RequestParam(name = "k", required = false) Optional<String> k,
        @RequestParam(name = "l", required = false) Optional<String> l,
        @RequestParam(name = "p", required = false) Optional<String> p
    ) throws IOException {
        if (k.isPresent() && l.isPresent() && p.isPresent())
            return ResponseEntity.ok(service.getFilteredGeneralGroupSchedule(generalGroupName, k.get(), l.get(), p.get()));
        return ResponseEntity.ok(service.getGeneralGroupSchedule(generalGroupName));
    }

    @GetMapping("/hours")
    public ResponseEntity<String> getListOfHours() {
        return ResponseEntity.ok().build();
    }

}
