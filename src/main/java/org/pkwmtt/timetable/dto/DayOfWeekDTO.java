package org.pkwmtt.timetable.dto;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.timetable.enums.TypeOfWeek;
import org.pkwmtt.timetable.objects.CustomSubjectDetails;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@Data
public class DayOfWeekDTO {
    private final String name;
    @Setter
    private List<SubjectDTO> odd;
    @Setter
    private List<SubjectDTO> even;
    
    public DayOfWeekDTO (String name) {
        this.name = name;
        odd = new ArrayList<>();
        even = new ArrayList<>();
    }
    
    /**
     * Add subject by week Type
     *
     * @param subjectDTO - subject
     * @param typeOfWeek - type of week
     */
    public void add (SubjectDTO subjectDTO, TypeOfWeek typeOfWeek) {
        switch (typeOfWeek) {
            case EVEN -> this.even.add(subjectDTO);
            case ODD -> this.odd.add(subjectDTO);
            case BOTH -> {
                this.even.add(subjectDTO);
                this.odd.add(subjectDTO);
            }
        }
    }
    
    public void deleteSubjectTypesFromNames () {
        even.forEach(SubjectDTO::deleteTypeAndUnnecessaryCharactersFromName);
        odd.forEach(SubjectDTO::deleteTypeAndUnnecessaryCharactersFromName);
    }
    
    /**
     * Filters both odd- and even-week subject lists,
     * keeping only those entries that belong exclusively
     * to the specified group code.
     *
     * @param group the full group identifier (e.g., "K03"),
     *              where the first character is the group letter
     *              and the last character is the subgroup number
     */
    public void filterByGroup (String group) {
        var groupCharAndTargetNumber = getGroupCharAndTargetNumber(group);
        
        // Apply the filter to both odd- and even-week lists
        odd = filter(odd, groupCharAndTargetNumber.getFirst(), groupCharAndTargetNumber.getSecond());
        even = filter(even, groupCharAndTargetNumber.getFirst(), groupCharAndTargetNumber.getSecond());
        
    }
    
    public void filterByGroup (String subGroup, List<CustomSubjectDetails> customSubjects) {
        var groupCharAndTargetNumber = getGroupCharAndTargetNumber(subGroup);

        // Apply the filter to both odd- and even-week lists
        odd = filter(
          odd, groupCharAndTargetNumber.getFirst(), groupCharAndTargetNumber.getSecond(), customSubjects
            .stream()
            .filter(customSubject -> customSubject.getTypeOfWeek().equals(TypeOfWeek.ODD))
            .toList()
        );
        
        even = filter(
          even, groupCharAndTargetNumber.getFirst(), groupCharAndTargetNumber.getSecond(), customSubjects
            .stream()
            .filter(customSubject -> customSubject.getTypeOfWeek().equals(TypeOfWeek.EVEN))
            .toList()
        );
        
    }
    
    private Pair<String, String> getGroupCharAndTargetNumber (String group) {
        // Delete first character if group starts 'G'
        if (group.charAt(0) == 'G' && group.length() > 3) {
            group = group.substring(1);
        }
        
        // Extract the group letter (e.g., "K" from "K03")
        var groupChar = String.valueOf(group.charAt(0));
        
        // Extract the subgroup digit (e.g., "3" from "K03")
        var targetNumber = String.valueOf(group.charAt(group.length() - 1));
        return Pair.of(groupChar, targetNumber);
    }
    
    /**
     * Returns a new list containing only those SubjectDTO items
     * whose type string matches exclusively the target group code or doesn't have group at all.
     *
     * @param list         the original list of subjects (odd or even week)
     * @param groupName    the group letter (e.g., "K")
     * @param targetNumber the subgroup digit to keep (e.g., "3")
     * @return a filtered list of SubjectDTO
     */
    private List<SubjectDTO> filter (List<SubjectDTO> list, String groupName, String targetNumber) {
        return list.stream().filter(item -> hasOnlyTargetGroup(item.getName(), groupName, targetNumber)).toList();
    }
    
    /**
     * Filter by subgroup char and number
     *
     * @param list           list of subjects for specific day
     * @param groupName      - name of subgroup
     * @param targetNumber   - number fo subgroup
     * @param customSubjects - custom subjects added by user
     * @return modified list of subjects
     */
    private List<SubjectDTO> filter (List<SubjectDTO> list,
                                     String groupName,
                                     String targetNumber,
                                     List<CustomSubjectDetails> customSubjects) {
        
        
        list = list
          .stream()
          .filter(item -> hasOnlyTargetGroup(item.getName(), groupName, targetNumber)) // K04 -> usun K != 4
          .collect(Collectors.toList());
        
        for (var customSubject : customSubjects) {
            list.add(customSubject.getSubject().setCustom(true));
        }
        
        list.sort(Comparator.comparingInt(SubjectDTO::getRowId));
        
        return list;
    }
    
    /**
     * Checks if the given element string contains no other codes for the same group.*
     *
     * @param element      the subject type string (e.g., "Mechatronika K03")
     * @param groupName    the group letter (e.g., "K")
     * @param targetNumber the digit we want to allow (e.g., "3")
     * @return true if no non-target subgroup codes are present
     */
    private boolean hasOnlyTargetGroup (String element, String groupName, String targetNumber) {
        var pattern = Pattern.compile(String.format("\\bG?[%s]0[1-9]\\b", Pattern.quote(groupName)));
        var matcher = pattern.matcher(element);
        if (!matcher.find()) {
            return true;
        }
        
        pattern = Pattern.compile(String.format("%s0%s", Pattern.quote(groupName), Pattern.quote(targetNumber)));
        matcher = pattern.matcher(element);
        return matcher.find();
    }
    
}