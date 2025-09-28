package org.pkwmtt.timetable.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Getter
public class CustomSubjectFilterDTO {
    private final String name;
    private final String generalGroup;
    private final String subGroup;
}
