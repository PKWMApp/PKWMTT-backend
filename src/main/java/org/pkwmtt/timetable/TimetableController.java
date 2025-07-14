package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/pkmwtt/api/v1/timetables")
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService service;

    @GetMapping("/{generalGroupName}")
    public ResponseEntity<TimetableDTO> getGeneralGroupSchedule(
        @PathVariable String generalGroupName,
        @RequestParam(name = "k", required = false) Optional<String> k,
        @RequestParam(name = "l", required = false) Optional<String> l,
        @RequestParam(name = "p", required = false) Optional<String> p
    ) throws WebPageContentNotAvailableException {
        if (k.isPresent() && l.isPresent() && p.isPresent())
            return ResponseEntity.ok(service.getFilteredGeneralGroupSchedule(generalGroupName, k.get(), l.get(), p.get()));
        return ResponseEntity.ok(service.getGeneralGroupSchedule(generalGroupName));
    }

    @GetMapping("/hours")
    public ResponseEntity<List<String>> getListOfHours() throws WebPageContentNotAvailableException {
        return ResponseEntity.ok(service.getListOfHours());
    }

    @GetMapping("/groups/general")
    public ResponseEntity<List<String>> getListOfGeneralGroups() {
        var result = new java.util.ArrayList<>(service.getGeneralGroupsList().keySet().stream().toList());
        Collections.sort(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/groups/{generalGroupName}")
    public ResponseEntity<List<String>> getListOfAvailableGroups(@PathVariable String generalGroupName) throws JsonProcessingException {
        return ResponseEntity.ok(service.getAvailableSubGroups(generalGroupName));
    }

    @ExceptionHandler(WebPageContentNotAvailableException.class)
    public ResponseEntity<String> handleWebPageContentNotAvailableException(WebPageContentNotAvailableException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Void> handleJsonProcessingException() {
        return ResponseEntity.internalServerError().build();
    }

}
