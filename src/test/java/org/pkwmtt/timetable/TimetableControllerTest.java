package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pkwmtt.exceptions.ErrorResponseDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimetableControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetGeneralGroupScheduleFiltered_withOptionalParams() {
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/12K1?sub=K01&sub=L01&sub=P01", port);

        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getData().size());
        assertEquals(12, response.getBody().getData().getFirst().getOdd().size());
        assertEquals(6, response.getBody().getData().getFirst().getEven().size());
    }

    @Test
    public void testGetGeneralGroupScheduleFiltered_withoutParams() throws JsonProcessingException {
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/12K1", port);

        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ObjectMapper mapper = new ObjectMapper();

        var result = mapper.writeValueAsString(response.getBody());
        System.out.println(result);

    }

    @Test
    public void shouldReturnListOfGeneralGroups() {
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/groups/general", port);

        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<String> result = Arrays.asList(response.getBody());
        assertNotNull(result);
        result.forEach(System.out::println);
    }

    @Test
    public void shouldReturnListOfSubgroupsForGeneralGroup() {
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/groups/12K1", port);

        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);

        System.out.println(Arrays.toString(response.getBody()));
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void textException_SpecifiedGeneralGroupDoesntExistsException() {
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/groups/XXXX", port);
        ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(url, ErrorResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getMessage()).contains("Specified general group doesn't exists");
        assertThat(response.getBody().getTimestamp()).isBefore(LocalDateTime.now());
    }

}