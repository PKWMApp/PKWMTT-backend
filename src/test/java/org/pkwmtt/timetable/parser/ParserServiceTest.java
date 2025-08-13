package org.pkwmtt.timetable.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.runners.Suite;
import org.pkwmtt.ValuesForTest;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Suite.SuiteClasses(TimetableParserService.class)
@ActiveProfiles("test")
@EnableWireMock({
        @ConfigureWireMock(
                name = "my-mock",
                port = 8888)
})
class ParserServiceTest {
    TimetableParserService parserService;

    {
        parserService = new TimetableParserService();
    }

    @Value("${main.url:https://podzial.mech.pk.edu.pl/stacjonarne/html/}")
    private String mainUrl;

    @Test
    public void checkParserDataFor12K1_Monday_First() throws IOException {
        //fetch 12K1
        Document document = Jsoup
            .connect("https://podzial.mech.pk.edu.pl/stacjonarne/html/plany/o25.html")
            .get();

        //Create object
        TimetableDTO timeTable = new TimetableDTO("12K1");

        //Call method
        timeTable.setData(parserService.parse(document.html()));

        //Tests
        assertEquals("12K1", timeTable.getName());
        assertEquals("Poniedziałek", timeTable.getData().getFirst().getName());
        assertEquals(5, timeTable.getData().size());
    }

    @Test
    public void isHoursListCorrect() throws IOException {

        //fetch data
        Document document = Jsoup
            .connect("https://podzial.mech.pk.edu.pl/stacjonarne/html/plany/o25.html")
            .get();


        //call function
        var result = parserService.parseHours(document.html());

        //Check first, last and middle element
        assertEquals("7:30- 8:15", result.getFirst());
        assertEquals("12:45-13:30", result.get(6));
        assertEquals("20:30-21:15", result.getLast());
    }

    @Test
    @WithMockUser
    public void isGeneralGroupListCorrect() throws IOException {
        //given
        initWireMock();

        //when
        //fetch data
        Document document = Jsoup
            .connect(mainUrl + "lista.html")
            .get();

        //call method
        var result = parserService.parseGeneralGroups(document.html());

        //then
        //Check if list contains specific elements
        assertTrue(result.containsKey("12K1"));
        assertTrue(result.containsKey("11A1"));
        assertTrue(result.containsKey("11K2"));
        assertEquals("plany/o25.html", result.get("12K1"));
    }

    private void initWireMock() {
        stubFor(get(urlPathMatching("/plany/o25.html"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/*")
                        .withBody(ValuesForTest.timetableHTML)));

        stubFor(get(urlPathMatching("/lista.html"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/*")
                        .withBody(ValuesForTest.listHTML)));
    }
}
