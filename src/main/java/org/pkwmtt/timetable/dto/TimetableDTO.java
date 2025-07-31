package org.pkwmtt.timetable.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimetableDTO {
    private String name;
    private List<DayOfWeekDTO> data;

    public TimetableDTO(String name) {
        this.name = name;
    }


}
