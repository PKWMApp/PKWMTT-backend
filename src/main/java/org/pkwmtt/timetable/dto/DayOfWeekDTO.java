package org.pkwmtt.timetable.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;

import java.util.ArrayList;
import java.util.List;

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

    public void addToEven(SubjectDTO subject) {
        even.add(subject);
    }

    public void addToOdd(SubjectDTO subject) {
        odd.add(subject);
    }

    public void filterByGroup(String group) {
        String groupName = Character.toString(group.charAt(0));
        String targetNumber = Character.toString(group.charAt(group.length() - 1));
        odd = filter(odd, groupName, targetNumber);
        even = filter(even, groupName, targetNumber);
    }


    private List<SubjectDTO> filter(List<SubjectDTO> list, String groupName, String targetNumber) {
        return list.stream().filter(item -> containsOthersGroups(item.getName(), groupName, targetNumber)).toList();
    }

    private boolean containsOthersGroups(String element, String groupName, String targetNumber) {
        int maxNumber = 7;
        for (int i = 1; i <= maxNumber; i++) {
            String number = Integer.toString(i);
            if (number.equals(targetNumber)) continue;
            if (element.contains(String.format("%s0%s", groupName, i)))
                return false;
        }
        return true;
    }

}

/*
     K01
     K02
     K03
     K04
     K05
     K06
 */