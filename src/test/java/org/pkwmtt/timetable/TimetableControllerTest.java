package org.pkwmtt.timetable;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimetableControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetGeneralGroupScheduleFiltered_withOptionalParams() throws Exception {
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/12K1?k=K01&l=L01&p=P01", port);

        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(12, response.getBody().getData().getFirst().getOdd().size());
        assertEquals(6, response.getBody().getData().getFirst().getEven().size());
        ObjectMapper mapper = new ObjectMapper();

        var result = mapper.writeValueAsString(response.getBody());
        System.out.println(result);

        assertTrue(result.contains("K01"));
        assertTrue(result.contains("L01"));
        assertTrue(result.contains("P01"));

        assertFalse(result.contains("K02"));
        assertFalse(result.contains("L02"));
        assertFalse(result.contains("P02"));

    }

    @Test
    public void testGetGeneralGroupScheduleFiltered_withoutParams()  {
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/12K1", port);

        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void shouldReturnListOfGeneralGroups(){
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/groups/general", port);
    }
}