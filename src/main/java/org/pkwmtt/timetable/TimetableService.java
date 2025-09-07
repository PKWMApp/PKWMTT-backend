package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.DayOfWeekDTO;
import org.pkwmtt.timetable.dto.SubjectDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TimetableService {
    private final TimetableCacheService cachedService;
    
    @Autowired
    TimetableService (TimetableCacheService cachedService) {
        this.cachedService = cachedService;
    }
    
    /**
     * Parses the timetable JSON to extract subgroup identifiers like K01, P03, GL04 using regex.
     *
     * @param generalGroupName group to analyze
     * @return sorted list of subgroup names found in the timetable
     * @throws JsonProcessingException if timetable conversion to JSON fails
     */
    public List<String> getAvailableSubGroups (String generalGroupName)
      throws JsonProcessingException, SpecifiedGeneralGroupDoesntExistsException, WebPageContentNotAvailableException {
        
        generalGroupName = generalGroupName.toUpperCase();
        TimetableDTO timetable = cachedService.getGeneralGroupSchedule(generalGroupName);
        
        ObjectMapper mapper = new ObjectMapper();
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
            if (text.startsWith("G")) {
                text = text.substring(1);
            }
            matchedGroups.add(text);
        }
        
        return matchedGroups.stream().sorted().toList();
    }
    
    
    /**
     * Retrieves timetable and filters entries based on subgroups parameters
     *
     * @param generalGroupName name of the general group
     * @param sub              subgroups list
     * @return filtered timetable
     * @throws WebPageContentNotAvailableException if source data can't be retrieved
     */
    public TimetableDTO getFilteredGeneralGroupSchedule (String generalGroupName, List<String> sub)
      throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException, JsonProcessingException {
        
        generalGroupName = generalGroupName.toUpperCase();
        
        //Check if specified subgroup is available for this generalGroup
        var subgroups = getAvailableSubGroups(generalGroupName);
        for (var group : sub) {
            if (!subgroups.contains(group)) {
                throw new SpecifiedSubGroupDoesntExistsException(group);
            }
        }
        
        List<DayOfWeekDTO> schedule = cachedService.getGeneralGroupSchedule(generalGroupName).getData();
        
        
        for (var day : schedule) {
            sub.forEach(day::filterByGroup);
        }
        
        schedule.forEach(DayOfWeekDTO::deleteSubjectTypesFromNames);
        
        return new TimetableDTO(generalGroupName, schedule);
    }
    
    /**
     * @return List of general group's names
     */
    public List<String> getGeneralGroupList () throws WebPageContentNotAvailableException {
        return cachedService.getGeneralGroupsMap().keySet().stream().sorted().collect(Collectors.toList());
    }
    
    public List<String> getListOfSubjects (String generalGroupName) {
        var subjectSet = new HashSet<String>();
        var schedule = cachedService.getGeneralGroupSchedule(generalGroupName);
        
        schedule.getData().forEach(day -> {
            day.getEven().forEach(subject -> addToSet(subjectSet, subject));
            day.getOdd().forEach(subject -> addToSet(subjectSet, subject));
        });
        
        return subjectSet.stream().toList();
    }
    
    private void addToSet (Set<String> subjectSet, SubjectDTO subject) {
        subject.deleteTypeAndUnnecessaryCharactersFromName();
        subjectSet.add(subject.getName());
    }
    
}
