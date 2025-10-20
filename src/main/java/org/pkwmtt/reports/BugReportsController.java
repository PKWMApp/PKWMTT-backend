package org.pkwmtt.reports;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.reports.dto.BugReportDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${apiPrefix}/bug-reports")
@RequiredArgsConstructor
//@RestController
public class BugReportsController {
    private final BugReportsService service;
    
    @PostMapping("/report")
    public ResponseEntity<Void> reportBug (@RequestBody BugReportDTO bugReportDTO) {
        service.addBugReport(bugReportDTO);
        return ResponseEntity.ok().build();
    }
}
