package org.pkwmtt.timetable.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimeTableDTO {
    private final String name;
    private List<DayOfWeekDTO> data;

    public TimeTableDTO(String name) {
        this.name = name;
    }

}
