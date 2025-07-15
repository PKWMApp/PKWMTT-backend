package org.pkwmtt.timetable;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CacheableTimetableServiceTest {
    @Autowired
    CacheableTimetableService service;

    @Test
    public void checkIfHoursListBodyIsNotNull() {
        var response = service.getListOfHours();

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void checkIfGeneralGroupListIsNotNull() {
        var response = service.getGeneralGroupsList();
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}