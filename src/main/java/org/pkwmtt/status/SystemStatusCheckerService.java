package org.pkwmtt.status;

import jakarta.annotation.PostConstruct;
import org.pkwmtt.mail.config.MailConfig;
import org.pkwmtt.timetable.TimetableCacheService;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.stereotype.Service;


@Service
public class SystemStatusCheckerService {
    
    private String mailingStatus;
    private String databaseStatus;
    private String cacheStatus;
    private String timetableStatus;
    
    SystemStatusCheckerService () {
        checkStatuses();
    }
    
    @PostConstruct
    private void checkStatuses () {
        mailingStatus = assignStatus(MailConfig.isEnabled());
        databaseStatus = assignStatus(DatabaseStatusChecker.isEnabled());
        timetableStatus = assignStatus(TimetableService.isEnabled());
        cacheStatus = assignStatus(TimetableCacheService.isCacheAvailable());
    }
    
    public String getStatus () {
        return String.format(
          """
             Server: ✅;
             Services:
                 Mail: %s
                 Database: %s,
                 Timetable: %s,
                 Cache: %s
            """, mailingStatus, databaseStatus, timetableStatus, cacheStatus
        );
    }
    
    
    private String assignStatus (boolean condition) {
        return condition ? "✅" : "❌";
    }
}
