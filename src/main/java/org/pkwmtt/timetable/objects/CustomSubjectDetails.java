package org.pkwmtt.timetable.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pkwmtt.timetable.dto.SubjectDTO;
import org.pkwmtt.timetable.enums.TypeOfWeek;

@Getter
@AllArgsConstructor
public class CustomSubjectDetails {
    SubjectDTO subject;
    String subGroup;
    int dayOfWeekNumber;
    TypeOfWeek typeOfWeek;
    
}
