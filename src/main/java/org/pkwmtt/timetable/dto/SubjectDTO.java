package org.pkwmtt.timetable.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
public class SubjectDTO {
    private final String name;
    private final String classroom;
    private final int rowId;
}
