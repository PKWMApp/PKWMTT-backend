package org.pkwmtt.timetable;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pkwmtt.timetable.dto.DayOfWeekDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.pkwmtt.timetable.parser.ParserService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final ParserService parser;

    public Map<String, String> getGeneralGroupsList() throws IOException {
        Document document = Jsoup
            .connect("http://podzial.mech.pk.edu.pl/stacjonarne/html/lista.html")
            .get();

        return parser.parseGeneralGroups(document.html());
    }

    public TimetableDTO getGeneralGroupSchedule(String generalGroupName) throws IOException {
        String url = getGeneralGroupsList().get(generalGroupName);
        Document document = Jsoup
            .connect(String.format("https://podzial.mech.pk.edu.pl/stacjonarne/html/%s", url))
            .get();

        return new TimetableDTO(generalGroupName, parser.parse(document.html()));
    }

    public TimetableDTO getFilteredGeneralGroupSchedule(String generalGroupName, String k, String l, String p) throws IOException {
        String url = getGeneralGroupsList().get(generalGroupName);
        Document document = Jsoup
            .connect(String.format("https://podzial.mech.pk.edu.pl/stacjonarne/html/%s", url))
            .get();

        List<DayOfWeekDTO> schedule = parser.parse(document.html());

        for (var day : schedule) {
            day.filterByGroup(k);
            day.filterByGroup(l);
            day.filterByGroup(p);
        }

        return new TimetableDTO(generalGroupName, schedule);
    }

    public List<String> getListOfHours () throws IOException {
        Document document = Jsoup
            .connect("https://podzial.mech.pk.edu.pl/stacjonarne/html/plany/o25.html")
            .get();

        return  parser.parseHours(document.html());
    }

}
