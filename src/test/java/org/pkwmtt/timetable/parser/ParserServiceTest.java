package org.pkwmtt.timetable.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.runners.Suite;

import javax.swing.text.BadLocationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Suite.SuiteClasses(ParserService.class)
class ParserServiceTest {
    @Test
    public void getParserData() throws IOException, BadLocationException {
        Document document = Jsoup
            .connect("https://podzial.mech.pk.edu.pl/stacjonarne/html/plany/o25.html")
            .get();
        ParserService parserService = new ParserService();
        System.out.println(parserService.parse(document.html()));

    }

    @Test
    public void getHoursList() throws IOException {
        ParserService parserService = new ParserService();

        //fetch data
        Document document = Jsoup
            .connect("https://podzial.mech.pk.edu.pl/stacjonarne/html/plany/o25.html")
            .get();

        //alter html to suitable form
        String cleanedHtml = parserService.cleanHtml(document.html());

        //call function
        var hours = parserService.getHours(cleanedHtml);

        //Check first, last and middle element
        assertEquals("7:30- 8:15", hours.getFirst());
        assertEquals("12:45-13:30", hours.get(6));
        assertEquals("20:30-21:15", hours.getLast());
    }

    @Test
    public void getGeneralGroupList() throws IOException {
        Document document = Jsoup
            .connect("http://podzial.mech.pk.edu.pl/stacjonarne/html/lista.html")
            .get();

    }
}