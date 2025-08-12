package org.pkwmtt.timetable;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pkwmtt.cache.CacheInspector;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class TimetableCacheServiceTest {
    @Autowired
    TimetableCacheService cachedService;
    
    @Autowired
    TimetableService service;
    
    @Autowired
    CacheInspector cacheInspector;
    
    
    @Test
    public void shouldHourListBePresentInCache () {
        //given
        String key = "hourList";
        cachedService.getListOfHours(); // call method to save data in cache
        
        //when
        Map<Object, Object> cacheData = cacheInspector.getAllEntries("timetables"); // get all keys saved in cache
        
        //then
        assertNotNull(cacheData);
        assertTrue(cacheData.containsKey(key));
        assertNotNull(cacheData.get(key));
    }
    
    
    @Test
    public void shouldReturnGeneralGroupsMap () {
        //given
        Map<String, String> result = cachedService.getGeneralGroupsMap();
        
        //when
        
        //then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    
    @Test
    public void shouldGeneralGroupMapBePresentInCache () {
        //given
        String key = "generalGroupMap";
        cachedService.getGeneralGroupsMap(); // call method to save data in cache
        
        //when
        Map<Object, Object> cacheData = cacheInspector.getAllEntries("timetables"); // get all keys saved in cache
       
        //then
        assertNotNull(cacheData);
        assertTrue(cacheData.containsKey(key));
        assertNotNull(cacheData.get(key));
    }
    
    @Test
    public void shouldReturnRandomGeneralGroupSchedule () {
        //given
        List<String> generalGroupList = service.getGeneralGroupList();
        String generalGroupName = generalGroupList.get((int) (Math.random() * generalGroupList.size())); // get random general group
        
        //when
        var result = cachedService.getGeneralGroupSchedule(generalGroupName);
        
        //then
        assertNotNull(result);
        assertInstanceOf(TimetableDTO.class, result);
    }
    
    @Test
    public void shouldRandomGeneralGroupScheduleBePresentInCache () {
        //given
        List<String> generalGroupList = service.getGeneralGroupList();
        
        String generalGroupName = generalGroupList.get((int) (Math.random() * generalGroupList.size())); // get random general group
        String key = "timetable_" + generalGroupName;
        
        cachedService.getGeneralGroupSchedule(generalGroupName); // call method to save data in cache
        
        //when
        Map<Object, Object> cacheData = cacheInspector.getAllEntries("timetables"); // get all keys saved in cache
        
        //then
        assertNotNull(cacheData);
        assertTrue(cacheData.containsKey(key));
        assertInstanceOf(String.class, cacheData.get(key));
    }
    
    
}