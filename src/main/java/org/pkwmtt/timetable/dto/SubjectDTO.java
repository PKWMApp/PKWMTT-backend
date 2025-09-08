package org.pkwmtt.timetable.dto;

import lombok.*;
import lombok.experimental.Accessors;
import org.pkwmtt.examCalendar.enums.SubjectType;

import java.util.regex.Pattern;

@Data
@Accessors(chain = true)
public class SubjectDTO {
    private String name;
    private String classroom;
    private int rowId;
    private SubjectType type;
    
    
    public void deleteTypeAndUnnecessaryCharactersFromName () {
        if (name.contains(" ")) {
            this.name = name.substring(0, name.indexOf(' '));
        }
        
        name = name.replaceAll("_", " ").replaceAll(Pattern.quote("("), "").replaceAll(Pattern.quote(")"), "");
    }
}
