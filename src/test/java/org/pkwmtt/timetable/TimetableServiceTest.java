package org.pkwmtt.timetable;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pkwmtt.ValuesForTest;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import test.TestConfig;

import java.util.List;
import java.util.regex.Pattern;

@SpringBootTest
class TimetableServiceTest extends TestConfig {
    
    @Autowired
    private TimetableService service;

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
    public void shouldReturnAvailableSubGroups () throws JsonProcessingException {
        //given
        var generalGroupName = "12K1";
        var regex = "^[A-Z]\\d{2}$";
        var pattern = Pattern.compile(regex);
        var expectedResult = List.of("K01", "K04", "L01", "L02", "L04", "P01", "P04");
        
        //when
        var result = service.getAvailableSubGroups(generalGroupName);
        
        //then
        assertThat(result).isEqualTo(expectedResult);

        //I don't know why it is in test. I think it is for debug?
//        result.forEach(item -> {
//            Matcher matcher = pattern.matcher(item);
//            if (!matcher.find()) {
//                fail("Wrong subgroup format");
//            }
//        });
    }
    
    
    @Test
    public void shouldThrow_SpecifiedGeneralGroupDoesntExistsException () {
        //given
        var subgroups = List.of("K01", "L01");
        var generalGroupName = "77Z3";
        //when
        
        //then
        assertThrows(
                SpecifiedGeneralGroupDoesntExistsException.class,
                () -> service.getFilteredGeneralGroupSchedule(generalGroupName, subgroups)
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
                SpecifiedSubGroupDoesntExistsException.class,
                () -> service.getFilteredGeneralGroupSchedule(generalGroupName, subgroups)
        );
    }
    
    @Test
    public void shouldReturnSortedGeneralGroupList () {
        //given
        var expectedResult = List.of(
                "11A1",
                "11K2",
                "12K1",
                "12K2",
                "12K3"
        );
        //when
        var result = service.getGeneralGroupList();

        //then
        assertAll(
                () -> assertNotNull(result),
                () -> assertFalse(result.isEmpty()),
                () -> assertEquals(expectedResult, result)
        );
    }
}