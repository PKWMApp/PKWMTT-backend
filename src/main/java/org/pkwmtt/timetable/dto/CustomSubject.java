package org.pkwmtt.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pkwmtt.timetable.enums.TypeOfWeek;

@Getter
@AllArgsConstructor
public class CustomSubject {
    SubjectDTO subject;
    String subGroup;
    int dayOfWeekNumber;
    TypeOfWeek typeOfWeek;
    
}
