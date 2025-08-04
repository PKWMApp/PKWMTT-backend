package org.pkwmtt.timetable.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@Getter
public class DayOfWeekDTO {
    private final String name;
    private List<SubjectDTO> odd;
    private List<SubjectDTO> even;

    public DayOfWeekDTO(String name) {
        this.name = name;
        odd = new ArrayList<>();
        even = new ArrayList<>();
    }


    public void add(SubjectDTO subjectDTO, boolean isNotOdd) {
        if (isNotOdd) {
            even.add(subjectDTO);
        } else {
            odd.add(subjectDTO);
        }
    }


    public void deleteSubjectTypesFromNames() {
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
    public void filterByGroup(String group) {
        // Delete first character if group starts 'G'
        if (group.charAt(0) == 'G' && group.length() > 3)
            group = group.substring(1);

        // Extract the group letter (e.g., "K" from "K03")
        String groupName = Character.toString(group.charAt(0));

        // Extract the subgroup digit (e.g., "3" from "K03")
        String targetNumber = Character.toString(group.charAt(group.length() - 1));

        // Apply the filter to both odd- and even-week lists
        odd = filter(odd, groupName, targetNumber);
        even = filter(even, groupName, targetNumber);

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
    private List<SubjectDTO> filter(List<SubjectDTO> list, String groupName, String targetNumber) {

        list = list.stream()
            // Keep only items that have no other subgroup codes
            .filter(
                item ->
                    hasOnlyTargetGroup(
                        item.getName(),
                        groupName,
                        targetNumber
                    )
            ).toList();

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
    private boolean hasOnlyTargetGroup(String element, String groupName, String targetNumber) {
        Pattern pattern = Pattern.compile(String.format("\\bG?[%s]0[1-9]\\b", groupName));
        Matcher matcher = pattern.matcher(element);
        if (!matcher.find())
            return true;

        pattern = Pattern.compile(String.format("%s0%s", groupName, targetNumber));
        matcher = pattern.matcher(element);
        return matcher.find();
    }

}