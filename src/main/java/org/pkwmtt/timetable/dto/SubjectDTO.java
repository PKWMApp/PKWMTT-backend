package org.pkwmtt.timetable.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SubjectDTO {
    private String name;
    private String classroom;
    private int rowId;
}
