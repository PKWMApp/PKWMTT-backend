package org.pkwmtt.timetable;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SpringBootTest
class TimetableServiceTest {
    
    @Autowired
    private TimetableService service;
    
    @Test
    public void shouldReturnAvailableSubGroups () throws JsonProcessingException {
        //given
        String generalGroupName = "12K1";
        String regex = "^[A-Z]\\d{2}$";
        Pattern pattern = Pattern.compile(regex);
        
        //when
        var result = service.getAvailableSubGroups(generalGroupName);
        
        //then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        result.forEach(item -> {
            Matcher matcher = pattern.matcher(item);
            if (!matcher.find()) {
                fail("Wrong subgroup format");
            }
        });
        
    }
    
    
    @Test
    public void shouldThrow_SpecifiedGeneralGroupDoesntExistsException () {
        //given
        List<String> subgroups = List.of("K01", "L01");
        String generalGroupName = "77Z3";
        //when
        
        //then
        assertThrows(
          SpecifiedGeneralGroupDoesntExistsException.class, () -> service.getFilteredGeneralGroupSchedule(generalGroupName, subgroups)
        );
        
    }
    
    @Test
    public void shouldThrow_SpecifiedSubGroupDoesntExistsException () {
        //given
        List<String> subgroups = List.of("Z01", "XCD");
        String generalGroupName = "12K1";
        //when
        
        //then
        assertThrows(
          SpecifiedSubGroupDoesntExistsException.class, () -> service.getFilteredGeneralGroupSchedule(generalGroupName, subgroups)
        );
        
    }
    
    
    @Test
    public void shouldReturnSortedGeneralGroupList () {
        //given
        List<String> result = service.getGeneralGroupList();
        List<String> originalResult = new ArrayList<>(result);
        //when
        Collections.sort(result);
        
        //then
        assertEquals(originalResult, result);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
    }
    
}