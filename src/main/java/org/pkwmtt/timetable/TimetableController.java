package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/pkmwtt/api/v1/timetables")
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService service;
    private final TimetableCacheService cacheableService;

    /**
     * Provide schedule of specified group and filters if all provided
     *
     * @param generalGroupName name of general group
     * @param sub              list of subgroups
     * @return schedule of specified group with provided filters
     * @throws WebPageContentNotAvailableException .
     */
    @GetMapping("/{generalGroupName}")
    public ResponseEntity<TimetableDTO> getGeneralGroupSchedule(@PathVariable String generalGroupName, @RequestParam(required = false) List<String> sub)
        throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException {
        if (sub == null || sub.isEmpty())
            return ResponseEntity.ok(cacheableService.getGeneralGroupSchedule(generalGroupName));

        //todo delete
        sub = sub.stream().map(String::toUpperCase).toList();

        return ResponseEntity.ok(service.getFilteredGeneralGroupSchedule(generalGroupName.toUpperCase(), sub));
    }

    /**
     * Provides list of schedule hours
     *
     * @return list of houts
     * @throws WebPageContentNotAvailableException .
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
     * @throws JsonProcessingException .
     */
    @GetMapping("/groups/{generalGroupName}")
    public ResponseEntity<List<String>> getListOfAvailableGroups(@PathVariable String generalGroupName)
        throws JsonProcessingException, SpecifiedGeneralGroupDoesntExistsException, WebPageContentNotAvailableException {
        return ResponseEntity.ok(service.getAvailableSubGroups(generalGroupName.toUpperCase()));
    }



}
