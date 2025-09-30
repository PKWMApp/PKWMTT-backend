package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.CustomSubjectFilterDTO;
import org.pkwmtt.timetable.dto.DayOfWeekDTO;
import org.pkwmtt.timetable.dto.SubjectDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.pkwmtt.timetable.enums.TypeOfWeek;
import org.pkwmtt.timetable.objects.CustomSubjectDetails;
import org.pkwmtt.timetable.parser.TimetableParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
        //Uppercase name to assure match
        generalGroupName = generalGroupName.toUpperCase();
        
        //Check if specified subgroup is available for general group or else throw
        checkSubGroupAvailability(generalGroupName, subgroup);
        
        //Get user's schedule
        List<DayOfWeekDTO> schedule = cachedService.getGeneralGroupSchedule(generalGroupName).getData();
        //Go through schedule and extract customSubject details
        List<CustomSubjectDetails> customSubjectsDetails =
          createListOfCustomSchedulesDetails(generalGroupName, customSubjectFilters, schedule);
        
        return filterSchedule(schedule, subgroup, generalGroupName, customSubjectsDetails);
    }
    
    private List<CustomSubjectDetails> createListOfCustomSchedulesDetails (String generalGroupName,
                                                                           List<CustomSubjectFilterDTO> customSubjectFilters,
                                                                           List<DayOfWeekDTO> schedule) {
        List<CustomSubjectDetails> customSubjectsDetails = new ArrayList<>();
        customSubjectFilters.forEach(customFilter -> {
            
            //Get schedule for specified filter
            List<DayOfWeekDTO> customSubjectSchedule = customFilter
              .getGeneralGroup()
              .equals(generalGroupName) ? schedule : cachedService
              .getGeneralGroupSchedule(customFilter.getGeneralGroup())
              .getData();
            
            //Add detail like classroom and rowId
            //Go by days: Monday, Tuesday etc...
            for (int i = 0; i < customSubjectSchedule.size(); i++) {
                //Find subjects matching filters
                customSubjectsDetails.addAll(
                  searchDayOfWeekAndAddCustomSubjectsDetails(
                    customSubjectSchedule.get(i).getEven(), customFilter, i,
                    TypeOfWeek.EVEN
                  ));
                
                customSubjectsDetails.addAll(
                  searchDayOfWeekAndAddCustomSubjectsDetails(
                    customSubjectSchedule.get(i).getOdd(), customFilter, i,
                    TypeOfWeek.ODD
                  ));
            }
        });
        return customSubjectsDetails;
    }
    
    private List<CustomSubjectDetails> searchDayOfWeekAndAddCustomSubjectsDetails (List<SubjectDTO> day,
                                                                                   CustomSubjectFilterDTO customFilter,
                                                                                   int dayIndex,
                                                                                   TypeOfWeek typeOfWeek) {
        List<SubjectDTO> matches = switch (TimetableParserService.extractSubjectTypeFromName(
          customFilter.getSubGroup())) {
            //Filter by matching name and subgroup from customFilter
            //If exercises,lecture or seminar just compare type of subject
            case EXERCISES, LECTURE, SEMINAR -> day
              .stream()
              .filter(item -> (item
                .getName()
                .contains(customFilter.getName()) &&
                TimetableParserService
                  .extractSubjectTypeFromName(item.getName())
                  .equals(
                    TimetableParserService
                      .extractSubjectTypeFromName(customFilter.getSubGroup()))
              ))
              .toList();
            
            //Filter by matching name and subgroup from customFilter
            //if LKP groups compare group type and number
            default -> day
              .stream()
              .filter(item ->
                        (item
                          .getName()
                          .contains(customFilter.getName()) &&
                          item
                            .getName()
                            .contains(customFilter.getSubGroup()))).toList();
        };
        
        if (!matches.isEmpty()) {
            return matches
              .stream()
              .map((item) -> new CustomSubjectDetails(item, customFilter.getSubGroup(), dayIndex, typeOfWeek))
              .toList();
        }
        return new ArrayList<>();
    }
    
    private TimetableDTO filterSchedule (List<DayOfWeekDTO> schedule,
                                         List<String> subgroups,
                                         String generalGroupName,
                                         List<CustomSubjectDetails> customSubjectsDetails) {
        
        //Go through user's schedule day by day
        for (int i = 0; i < schedule.size(); i++) {
            var day = schedule.get(i);
            deleteSubjectsCollidingWithCustomFilters(customSubjectsDetails, day);
            
            //Filter by user's subgroups
            filterDayBySubgroupsWithSeminarsExercisesAndLectures(
              subgroups, customSubjectsDetails, day, i);
        }
        
        schedule.forEach(DayOfWeekDTO::deleteSubjectTypesFromNames);
        
        return new TimetableDTO(generalGroupName, schedule);
    }
    
    private void filterDayByUsersSubgroups (List<String> subgroups,
                                            List<CustomSubjectDetails> customSubjectsDetails,
                                            DayOfWeekDTO day, int dayIndex) {
        subgroups.forEach(subgroup -> {
            if (customSubjectsDetails.isEmpty()) {
                day.filterByGroup(subgroup);
                return;
            }
            
            var customSubjectsByDay = customSubjectsDetails.stream()
              //Compare day of week and subgroup
              .filter(subject -> subject.getSubGroup().charAt(0) == subgroup.charAt(0)) // match subgroup
              .filter(subject -> subject.getDayOfWeekNumber() == dayIndex) // match day of week
              .toList();
            
            
            day.filterByGroup(subgroup, customSubjectsByDay);
        });
    }
    
    private void filterDayBySubgroupsWithSeminarsExercisesAndLectures (List<String> subgroups,
                                                                       List<CustomSubjectDetails> customSubjectsDetails,
                                                                       DayOfWeekDTO day, int dayIndex) {
        
        Set<String> SCWgroups = new HashSet<>(
          customSubjectsDetails.stream().map(CustomSubjectDetails::getSubGroup)
            .map(item ->
                   switch (TimetableParserService.extractSubjectTypeFromName(item)) {
                       case SEMINAR -> "S";
                       case EXERCISES -> "Ć";
                       case LECTURE -> "W";
                       default -> null;
                   }
            )
            .filter(Objects::nonNull)
            .toList());
        
        List<String> effectiveSubgroups = new ArrayList<>(subgroups);
        effectiveSubgroups.addAll(SCWgroups);
        
        filterDayByUsersSubgroups(effectiveSubgroups, customSubjectsDetails, day, dayIndex);
    }
    
    
    private void deleteSubjectsCollidingWithCustomFilters (List<CustomSubjectDetails> customSubjectsDetails,
                                                           DayOfWeekDTO day) {
        for (CustomSubjectDetails customSubjectDetail : customSubjectsDetails) {
            customSubjectDetail.getSubject().deleteTypeAndUnnecessaryCharactersFromName();
            
            day.setEven(
              day
                .getEven()
                .stream()
                .filter(
                  subject -> !(subject
                    .getName()
                    .contains(customSubjectDetail.getSubject().getName())
                    && subjectsAreSameType(subject, customSubjectDetail))
                ).toList());
            
            day.setOdd(day
                         .getOdd()
                         .stream()
                         .filter(
                           subject -> !(subject.getName().contains(customSubjectDetail.getSubject().getName())
                             && subjectsAreSameType(subject, customSubjectDetail))
                         ).toList());
            
        }
    }
    
    private boolean subjectsAreSameType (SubjectDTO subject, CustomSubjectDetails customSubjectDetails) {
        var subjectType = TimetableParserService.extractSubjectTypeFromName(subject.getName());
        var customSubjectType = TimetableParserService.extractSubjectTypeFromName(
          customSubjectDetails.getSubGroup());
        return subjectType.equals(customSubjectType);
        
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
