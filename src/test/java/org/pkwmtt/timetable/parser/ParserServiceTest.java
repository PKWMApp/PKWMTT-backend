package org.pkwmtt.timetable.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.runners.Suite;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Suite.SuiteClasses(TimetableParserService.class)
class ParserServiceTest {
    TimetableParserService parserService;

    {
        parserService = new TimetableParserService();
    }


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
        //fetch data
        Document document = Jsoup
            .connect("http://podzial.mech.pk.edu.pl/stacjonarne/html/lista.html")
            .get();

        //call method
        var result = parserService.parseGeneralGroups(document.html());
        //Check if list contains specific elements
        assertTrue(result.containsKey("12K1"));
        assertTrue(result.containsKey("11A1"));
        assertTrue(result.containsKey("11K2"));
        assertEquals("plany/o25.html", result.get("12K1"));
    }
}
