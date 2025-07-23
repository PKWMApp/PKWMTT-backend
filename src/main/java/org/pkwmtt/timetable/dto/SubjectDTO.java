package org.pkwmtt.timetable.dto;

import lombok.*;
import org.pkwmtt.timetable.enums.SubjectType;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SubjectDTO {
    private String name;
    private String classroom;
    private int rowId;
    private SubjectType type;


    public void deleteTypeFromName() {
        if (name.contains(" "))
            this.name = name.substring(name.indexOf(' '));
    }
}
