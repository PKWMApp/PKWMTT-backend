package org.pkwmtt.timetable.dto;

import lombok.*;
import org.pkwmtt.enums.SubjectType;

import java.util.regex.Pattern;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SubjectDTO {
    private String name;
    private String classroom;
    private int rowId;
    private SubjectType type;


    public void deleteTypeAndUnnecessaryCharactersFromName() {
        if (name.contains(" "))
            this.name = name.substring(0, name.indexOf(' '));

        name = name
            .replaceAll("_", " ")
            .replaceAll(Pattern.quote("("), "")
            .replaceAll(Pattern.quote(")"), "");
    }
}
