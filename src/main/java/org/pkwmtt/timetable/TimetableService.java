package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.*;
import org.pkwmtt.timetable.enums.TypeOfWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * @param subgroup         subgroups list
     * @return filtered timetable
     * @throws WebPageContentNotAvailableException if source data can't be retrieved
     */
    public TimetableDTO getFilteredGeneralGroupSchedule (String generalGroupName,
                                                         List<String> subgroup,
                                                         List<CustomSubjectFilterDTO> customSubjectFilters)
      throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException, JsonProcessingException {
        generalGroupName = generalGroupName.toUpperCase();
        
        checkSubGroupAvailability(generalGroupName, subgroup);
        
        List<DayOfWeekDTO> schedule = cachedService.getGeneralGroupSchedule(generalGroupName).getData();
        List<CustomSubject> customSubjects = new ArrayList<>();
        
        //Mechatronika 13K2 K02
        String finalGeneralGroupName = generalGroupName;
        customSubjectFilters.forEach(customFilter -> {
            
            //Get schedule for specified filter
            List<DayOfWeekDTO> tempSchedule = customFilter
              .getGeneralGroup()
              .equals(finalGeneralGroupName) ? schedule : cachedService
              .getGeneralGroupSchedule(customFilter.getGeneralGroup())
              .getData();
            
            for (int i = 0; i < tempSchedule.size(); i++) {
                int finalI = i;
                
                tempSchedule.get(i).getEven().forEach(subject -> {
                    var customSubject = createCustomSubject(TypeOfWeek.EVEN, subject, customFilter, finalI);
                    
                    if (customSubject != null) {
                        customSubjects.add(customSubject);
                    }
                });
                
                tempSchedule.get(i).getOdd().forEach(subject -> {
                    var customSubject = createCustomSubject(TypeOfWeek.ODD, subject, customFilter, finalI);
                    
                    if (customSubject != null) {
                        customSubjects.add(customSubject);
                    }
                });
            }
            
        });
        
        return filterSchedule(schedule, subgroup, generalGroupName, customSubjects);
    }
    
    private CustomSubject createCustomSubject (TypeOfWeek type,
                                               SubjectDTO subject,
                                               CustomSubjectFilterDTO customFilter,
                                               int i) {
        if (subject.getName().contains(customFilter.getName())) {
            if (subject.getName().contains(customFilter.getSubGroup())) {
                return new CustomSubject(subject, customFilter.getSubGroup(), i, type);
            }
        }
        return null;
    }
    
    private TimetableDTO filterSchedule (List<DayOfWeekDTO> schedule,
                                         List<String> subgroups,
                                         String generalGroupName,
                                         List<CustomSubject> customSubjects) {
        for (int i = 0; i < schedule.size(); i++) {
            var day = schedule.get(i);
            
            for (CustomSubject customSubject : customSubjects) {
                customSubject.getSubject().deleteTypeAndUnnecessaryCharactersFromName();
                
                day.setEven(day
                              .getEven()
                              .stream()
                              .filter(
                                subject -> !subject.getName().contains(customSubject.getSubject().getName()))
                              .toList());
                
                day.setOdd(day
                             .getOdd()
                             .stream()
                             .filter(subject -> !subject.getName().contains(customSubject.getSubject().getName()))
                             .toList());
                
            }
            
            int finalI = i;
            subgroups.forEach(subgroup -> {
                if (customSubjects.isEmpty()) {
                    day.filterByGroup(subgroup);
                    return;
                }
                
                var customSubjectsByDay = customSubjects.stream()
                  //Compare day of week to get only matching days
                  .filter(subject -> subject.getSubGroup().charAt(0) == subgroup.charAt(0)) // match subgroup
                  .filter(subject -> subject.getDayOfWeekNumber() == finalI) // match day of week
                  .toList();
                
                day.filterByGroup(subgroup, customSubjectsByDay);
            });
            
        }
        
        schedule.forEach(DayOfWeekDTO::deleteSubjectTypesFromNames);
        
        return new TimetableDTO(generalGroupName, schedule);
    }
    
    private void checkSubGroupAvailability (String generalGroupName, List<String> subgroup)
      throws JsonProcessingException {
        //Check if specified subgroup is available for this generalGroup
        var subgroups = getAvailableSubGroups(generalGroupName);
        for (var group : subgroup) {
            if (!subgroups.contains(group)) {
                throw new SpecifiedSubGroupDoesntExistsException(group);
            }
        }
    }
    
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
