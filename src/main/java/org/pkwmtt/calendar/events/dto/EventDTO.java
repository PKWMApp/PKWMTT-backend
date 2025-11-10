package org.pkwmtt.calendar.events.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class EventDTO {
    String title;
    String description;
    Date startDate;
    Date endDate;
}
