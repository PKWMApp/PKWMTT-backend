package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/pkmwtt/api/v1/timetables")
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService service;
    private final TimetableCacheService cachedService;

    /**
     * Provide schedule of specified group and filters if all provided
     *
     * @param generalGroupName name of general group
     * @param subgroups        list of subgroups
     * @return schedule of specified group with provided filters
     * @throws WebPageContentNotAvailableException .
     */
    @GetMapping("/{generalGroupName}")
    public ResponseEntity<TimetableDTO> getGeneralGroupSchedule (@PathVariable String generalGroupName, @RequestParam(required = false, name = "sub") List<String> subgroups)
      throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException,
             SpecifiedSubGroupDoesntExistsException, JsonProcessingException {

        if (isNull(subgroups) || subgroups.isEmpty()) {
            return ResponseEntity.ok(cachedService.getGeneralGroupSchedule(generalGroupName));
        }
        return ResponseEntity.ok(service.getFilteredGeneralGroupSchedule(
          generalGroupName.toUpperCase(),
          subgroups
        ));
    }

    /**
     * Provides list of schedule hours
     *
     * @return list of houts
     * @throws WebPageContentNotAvailableException .
     */
    @GetMapping("/hours")
    public ResponseEntity<List<String>> getListOfHours ()
      throws WebPageContentNotAvailableException {
        return ResponseEntity.ok(cachedService.getListOfHours());
    }

    /**
     * Provides list of general groups
     *
     * @return list of general groups
     */
    @GetMapping("/groups/general")
    public ResponseEntity<List<String>> getListOfGeneralGroups ()
      throws WebPageContentNotAvailableException {
        return ResponseEntity.ok(service.getGeneralGroupList());
    }

    /**
     * Provides list of available subgroups for specified general group
     *
     * @param generalGroupName name of general group
     * @return list of available subgroups
     * @throws JsonProcessingException .
     */
    @GetMapping("/groups/{generalGroupName}")
    public ResponseEntity<List<String>> getListOfAvailableGroups (@PathVariable String generalGroupName)
      throws JsonProcessingException, SpecifiedGeneralGroupDoesntExistsException,
             WebPageContentNotAvailableException {
        return ResponseEntity.ok(service.getAvailableSubGroups(generalGroupName));
    }


}
