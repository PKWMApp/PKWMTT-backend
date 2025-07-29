package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.DayOfWeekDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableService {
    private final CacheableTimetableService cacheableTimetableService;

    /**
     * Parses the timetable JSON to extract subgroup identifiers like K01, P03, GL04 using regex.
     *
     * @param generalGroupName group to analyze
     * @return sorted list of subgroup names found in the timetable
     * @throws JsonProcessingException if timetable conversion to JSON fails
     */
    public List<String> getAvailableSubGroups(String generalGroupName)
        throws JsonProcessingException, SpecifiedGeneralGroupDoesntExistsException, WebPageContentNotAvailableException {
        ObjectMapper mapper = new ObjectMapper();
        TimetableDTO timetable = cacheableTimetableService.getGeneralGroupSchedule(generalGroupName);
        String timeTableAsJson = mapper.writeValueAsString(timetable);

        // Regex pattern for group codes like K01, GP03, L04, etc.
        String regex = "\\bG?[KPL]0[0-9]\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(timeTableAsJson);

        Set<String> matchedGroups = new HashSet<>();

        //Check if text starts with 'G' and delete it
        // to match frontend requirements
        String text;
        while (matcher.find()) {
            text = matcher.group();
            if (text.startsWith("G"))
                text = text.substring(1);
            matchedGroups.add(text);
        }

        List<String> result = new ArrayList<>(matchedGroups.stream().toList());
        Collections.sort(result);

        return result;
    }


    /**
     * Retrieves timetable and filters entries based on subgroups parameters
     *
     * @param generalGroupName name of the general group
     * @param sub              subgroups list
     * @return filtered timetable
     * @throws WebPageContentNotAvailableException if source data can't be retrieved
     */
    public TimetableDTO getFilteredGeneralGroupSchedule(String generalGroupName, List<String> sub) throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException {
        List<DayOfWeekDTO> schedule = cacheableTimetableService.getGeneralGroupSchedule(generalGroupName).getData();

        for (var day : schedule)
            sub.forEach(day::filterByGroup);

        schedule.forEach(DayOfWeekDTO::deleteSubjectTypesFromNames);

        return new TimetableDTO(generalGroupName, schedule);
    }


}
