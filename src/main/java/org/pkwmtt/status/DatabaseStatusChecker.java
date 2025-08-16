package org.pkwmtt.status;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Service
public class DatabaseStatusChecker {
    @Getter
    private static boolean enabled = false;
    
    @Autowired
    DatabaseStatusChecker (DataSource dataSource) {
        try {
            enabled = dataSource.getConnection().isValid(2);
        } catch (SQLException e) {
            log.error("Couldn't check database connection. Service will be unavailable");
        }
    }
}
