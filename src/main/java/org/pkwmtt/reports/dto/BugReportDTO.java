package org.pkwmtt.reports.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class BugReportDTO {
    int reportId;
    String userGroups;
    String description;
    Date IssuedAt;
}
