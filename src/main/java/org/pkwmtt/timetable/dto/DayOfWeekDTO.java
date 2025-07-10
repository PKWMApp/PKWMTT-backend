package org.pkwmtt.timetable.dto;

import lombok.Getter;
import lombok.Setter;

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
}
