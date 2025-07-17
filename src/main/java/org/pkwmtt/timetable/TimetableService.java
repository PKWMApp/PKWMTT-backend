package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.DayOfWeekDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.pkwmtt.timetable.parser.TimetableParserService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final CacheableTimetableService cacheableTimetableService;

    /**
     * Parses the timetable JSON to extract subgroup identifiers like K01, P03, L04 using regex.
     *
     * @param generalGroupName group to analyze
     * @return sorted list of subgroup names found in the timetable
     * @throws JsonProcessingException if timetable conversion to JSON fails
     */
    public List<String> getAvailableSubGroups(String generalGroupName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TimetableDTO timetable = cacheableTimetableService.getGeneralGroupSchedule(generalGroupName);
        String timeTableAsJson = mapper.writeValueAsString(timetable);

        // Regex pattern for group codes like K01, P03, L04, etc.
        String regex = "\\b[KPL]0[0-9]\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(timeTableAsJson);

        Set<String> matchedGroups = new HashSet<>();

        while (matcher.find())
            matchedGroups.add(matcher.group());

        List<String> result = new ArrayList<>(matchedGroups.stream().toList());
        Collections.sort(result);

        return result;
    }



    /**
     * Retrieves timetable and filters entries based on subgroup parameters (k, l, p).
     *
     * @param generalGroupName name of the general group
     * @param k                subgroup K code
     * @param l                subgroup L code
     * @param p                subgroup P code
     * @return filtered timetable
     * @throws WebPageContentNotAvailableException if source data can't be retrieved
     */
    public TimetableDTO getFilteredGeneralGroupSchedule(String generalGroupName, String k, String l, String p) throws WebPageContentNotAvailableException {
        List<DayOfWeekDTO> schedule = cacheableTimetableService.getGeneralGroupSchedule(generalGroupName).getData();

        for (var day : schedule) {
            day.filterByGroup(k);
            day.filterByGroup(l);
            day.filterByGroup(p);
        }

        return new TimetableDTO(generalGroupName, schedule);
    }



}
