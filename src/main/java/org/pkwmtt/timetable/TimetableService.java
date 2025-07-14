package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.DayOfWeekDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.pkwmtt.timetable.parser.ParserService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final ParserService parser;

    public Map<String, String> getGeneralGroupsList() throws WebPageContentNotAvailableException {
        Document document;
        try {
            document = Jsoup
                .connect("http://podzial.mech.pk.edu.pl/stacjonarne/html/lista.html")
                .get();
        } catch (IOException ioe) {
            throw new WebPageContentNotAvailableException();
        }

        return parser.parseGeneralGroups(document.html());
    }

    public List<String> getAvailableSubGroups(String generalGroupName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TimetableDTO timetable = getGeneralGroupSchedule(generalGroupName);
        String timeTableAsJson = mapper.writeValueAsString(timetable);

        // Regex pattern for group codes like K01, P03, L04, etc.
        String regex = "\\b[KPL]0[0-9a-zA-Z]\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(timeTableAsJson);

        Set<String> matchedGroups = new HashSet<>();

        while (matcher.find())
            matchedGroups.add(matcher.group());

        List<String> result = new ArrayList<>(matchedGroups.stream().toList());
        Collections.sort(result);

        return result;
    }

    public TimetableDTO getGeneralGroupSchedule(String generalGroupName) throws WebPageContentNotAvailableException {
        Document document;
        String url = getGeneralGroupsList().get(generalGroupName);
        try {
            document = Jsoup
                .connect(String.format("https://podzial.mech.pk.edu.pl/stacjonarne/html/%s", url))
                .get();

        } catch (IOException ioe) {
            throw new WebPageContentNotAvailableException();
        }

        return new TimetableDTO(generalGroupName, parser.parse(document.html()));
    }

    public TimetableDTO getFilteredGeneralGroupSchedule(String generalGroupName, String k, String l, String p) throws WebPageContentNotAvailableException {
        Document document;
        try {
            String url = getGeneralGroupsList().get(generalGroupName);

            document = Jsoup
                .connect(String.format("https://podzial.mech.pk.edu.pl/stacjonarne/html/%s", url))
                .get();
        } catch (IOException ioe) {
            throw new WebPageContentNotAvailableException();
        }

        List<DayOfWeekDTO> schedule = parser.parse(document.html());

        for (var day : schedule) {
            day.filterByGroup(k);
            day.filterByGroup(l);
            day.filterByGroup(p);
        }

        return new TimetableDTO(generalGroupName, schedule);
    }

    public List<String> getListOfHours() throws WebPageContentNotAvailableException {
        try {
            Document document = Jsoup
                .connect("https://podzial.mech.pk.edu.pl/stacjonarne/html/plany/o25.html")
                .get();

            return parser.parseHours(document.html());
        } catch (IOException ioe) {
            throw new WebPageContentNotAvailableException();
        }
    }

}
