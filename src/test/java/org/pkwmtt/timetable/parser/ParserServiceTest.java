package org.pkwmtt.timetable.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.runners.Suite;
import org.pkwmtt.timetable.dto.TimeTableDTO;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Suite.SuiteClasses(ParserService.class)
class ParserServiceTest {
    ParserService parserService;

    {
        parserService = new ParserService();
    }


    @Test
    public void checkParserDataFor12K1_Monday_First() throws IOException {
        //fetch 12K1
        Document document = Jsoup
            .connect("https://podzial.mech.pk.edu.pl/stacjonarne/html/plany/o25.html")
            .get();

        //Create object
        TimeTableDTO timeTable = new TimeTableDTO("12K1");

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

        //alter html to suitable form
        String cleanedHtml = parserService.cleanHtml(document.html());

        //call function
        var result = parserService.getHours(cleanedHtml);

        //Check first, last and middle element
        assertEquals("7:30- 8:15", result.getFirst());
        assertEquals("12:45-13:30", result.get(6));
        assertEquals("20:30-21:15", result.getLast());
    }

    @Test
    public void isGeneralGroupListCorrect() throws IOException {
        //fetch data
        Document document = Jsoup
            .connect("http://podzial.mech.pk.edu.pl/stacjonarne/html/lista.html")
            .get();

        //call method
        var result = parserService.parseGeneralGroupsHtmlToList(document.html());
        //Check if list contains specific elements
        assertTrue(result.containsKey("12K1"));
        assertTrue(result.containsKey("11A1"));
        assertTrue(result.containsKey("11K2"));
        assertEquals("plany/o25.html", result.get("12K1"));
    }
}
