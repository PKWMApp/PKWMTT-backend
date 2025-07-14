package org.pkwmtt.timetable;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    ) throws IOException {
        if (k.isPresent() && l.isPresent() && p.isPresent())
            return ResponseEntity.ok(service.getFilteredGeneralGroupSchedule(generalGroupName, k.get(), l.get(), p.get()));
        return ResponseEntity.ok(service.getGeneralGroupSchedule(generalGroupName));
    }

    @GetMapping("/hours")
    public ResponseEntity<List<String>> getListOfHours() throws WebPageContentNotAvailableException {
        return ResponseEntity.ok(service.getListOfHours());
    }


    @ExceptionHandler(WebPageContentNotAvailableException.class)
    public ResponseEntity<String> handleWebPageContentNotAvailableException(WebPageContentNotAvailableException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }



}
