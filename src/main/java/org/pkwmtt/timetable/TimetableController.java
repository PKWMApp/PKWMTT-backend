package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.ErrorResponseDTO;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pkmwtt/api/v1/timetables")
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService service;
    private final CacheableTimetableService cacheableService;

    /**
     * Provide schedule of specified group and filters if all provided
     *
     * @param generalGroupName name of general group
     * @param k                K group (f.e K02)
     * @param l                L group (f.e L02)
     * @param p                P group (f.e P02)
     * @return schedule of specified group with provided filters
     * @throws WebPageContentNotAvailableException
     */
    @GetMapping("/{generalGroupName}")
    public ResponseEntity<TimetableDTO> getGeneralGroupSchedule(
        @PathVariable String generalGroupName,
        @RequestParam(name = "k", required = false) Optional<String> k,
        @RequestParam(name = "l", required = false) Optional<String> l,
        @RequestParam(name = "p", required = false) Optional<String> p
    ) throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException {
        if (k.isPresent() && l.isPresent() && p.isPresent())
            return ResponseEntity.ok(service.getFilteredGeneralGroupSchedule(generalGroupName, k.get(), l.get(), p.get()));
        return ResponseEntity.ok(cacheableService.getGeneralGroupSchedule(generalGroupName));
    }

    /**
     * Provides list of schedule hours
     *
     * @return list of houts
     * @throws WebPageContentNotAvailableException
     */
    @GetMapping("/hours")
    public ResponseEntity<List<String>> getListOfHours() throws WebPageContentNotAvailableException {
        return ResponseEntity.ok(cacheableService.getListOfHours());
    }

    /**
     * Provides list of general groups
     *
     * @return list of general groups
     */
    @GetMapping("/groups/general")
    public ResponseEntity<List<String>> getListOfGeneralGroups() {
        var result = new java.util.ArrayList<>(cacheableService.getGeneralGroupsList().keySet().stream().toList());
        Collections.sort(result);
        return ResponseEntity.ok(result);
    }

    /**
     * Provides list of available subgroups for specified general group
     *
     * @param generalGroupName name of general group
     * @return list of available subgroups
     * @throws JsonProcessingException
     */
    @GetMapping("/groups/{generalGroupName}")
    public ResponseEntity<List<String>> getListOfAvailableGroups(@PathVariable String generalGroupName)
        throws JsonProcessingException, SpecifiedGeneralGroupDoesntExistsException, WebPageContentNotAvailableException {
        return ResponseEntity.ok(service.getAvailableSubGroups(generalGroupName));
    }

    @ExceptionHandler(WebPageContentNotAvailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponseDTO> handleWebPageContentNotAvailableException(WebPageContentNotAvailableException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleJsonProcessingException() {
        return new ResponseEntity<>(new ErrorResponseDTO(""), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(SpecifiedGeneralGroupDoesntExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleSpecifiedGeneralGroupDoesntExistsException(SpecifiedGeneralGroupDoesntExistsException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
