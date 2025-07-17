package org.pkwmtt.cache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CacheInspectorTest {

    @Autowired
    private CacheInspector inspector;

    @Test
    public void inspectCachedData_timetables(){
        inspector.printAllEntries("timetables");
    }

}