package org.pkwmtt.timetable;

import org.junit.jupiter.api.Test;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimetableControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetGeneralGroupScheduleFiltered_withOptionalParams() throws Exception {
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetable/12K1?k=K01&l=L01&p=P01", port);

        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        System.out.println(response.getBody().getName());
    }

    @Test
    public void testGetGeneralGroupScheduleFiltered_withoutParams() throws Exception {
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetable/12K1", port);

        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        System.out.println(response.getBody().getName());
    }
}