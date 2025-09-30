package org.pkwmtt.timetable.objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
    
    @Override
    public String toString () {
        JsonMapper mapper = new JsonMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
