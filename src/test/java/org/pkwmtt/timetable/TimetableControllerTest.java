package org.pkwmtt.timetable;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pkwmtt.ValuesForTest;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.TestConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TimetableControllerTest extends TestConfig {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void initWireMock() {
        EXTERNAL_SERVICE_API_MOCK.stubFor(get(urlPathMatching("/plany/o25.html"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/*")
                        .withBody(ValuesForTest.timetableHTML)));

        EXTERNAL_SERVICE_API_MOCK.stubFor(get(urlPathMatching("/lista.html"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/*")
                        .withBody(ValuesForTest.listHTML)));
    }
    
    @Test
    public void testGetGeneralGroupScheduleFiltered_withOptionalParams () {
        //given
        var url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/12K1?sub=K01&sub=L01&sub=P01",
                                   port
        );
        
        //when
        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);
        
        //then
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> {
                    var responseBody = response.getBody();
                    assertNotNull(responseBody);
                },
                () -> {
                    var responseData = response.getBody().getData();
                    assertEquals(5, responseData.size());
                    assertEquals(12, responseData.getFirst().getOdd().size());
                    assertEquals(6, responseData.getFirst().getEven().size());
                }
        );
    }
    
    @Test
    public void testGetGeneralGroupScheduleFiltered_withoutParams () {
        //given
        var url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/12K1", port);
        
        //when
        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);
        
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        assertEquals(5, response.getBody().getData().size()); // 5 days a week
    }
    
    @Test
    public void shouldReturnListOfGeneralGroups () {
        //given
        String url = String.format(
          "http://localhost:%s/pkmwtt/api/v1/timetables/groups/general",
          port
        );
        
        //when
        ResponseEntity<List<String>> response = restTemplate.exchange(
          url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
          }
        );
        
        //then
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    public void shouldReturnListOfSubgroupsForGeneralGroup () {
        //given
        String url = String.format(
          "http://localhost:%s/pkmwtt/api/v1/timetables/groups/12K1",
          port
        );
        
        //when
        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
        
        //then
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    public void shouldReturn_BadRequest_SpecifiedGeneralGroupDoesntExistsException () {
        //given
        String url = String.format(
          "http://localhost:%s/pkmwtt/api/v1/timetables/groups/XXXX",
          port
        );
        
        //when
        ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
          url,
          ErrorResponseDTO.class
        );
        
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getTimestamp()).isBefore(LocalDateTime.now());
    }
    
    @Test
    public void shouldReturn_BadRequest_SpecifiedSubGroupDoesntExistsException () {
        //given
        String url = String.format(
          "http://localhost:%s/pkmwtt/api/v1/timetables/12K1?sub=XXX",
          port
        );
        
        //when
        ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
          url,
          ErrorResponseDTO.class
        );
        
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getTimestamp()).isBefore(LocalDateTime.now());
    }
    
    @Test
    public void shouldReturn_ListOfHours () {
        //given
        String url = String.format("http://localhost:%s/pkmwtt/api/v1/timetables/hours", port);
        
        //when
        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
        
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        String regex = "\\b(?:[0-9]|1[0-9]|2[0-3]):[0-5][0-9]-(?:[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\\b";
        Pattern pattern = Pattern.compile(regex);
        Arrays.stream(response.getBody()).toList().forEach(item -> {
            Matcher matcher = pattern.matcher(item);
            if(!matcher.find()) fail("Wrong hour format");
        });
    }
}