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

    /**
     * Retrieves a mapping of general group names to their corresponding timetable URLs.
     * @return map of group names to URLs
     * @throws WebPageContentNotAvailableException if the source page can't be fetched
     */
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

    /**
     * Parses the timetable JSON to extract subgroup identifiers like K01, P03, L04 using regex.
     * @param generalGroupName group to analyze
     * @return sorted list of subgroup names found in the timetable
     * @throws JsonProcessingException if timetable conversion to JSON fails
     */
    public List<String> getAvailableSubGroups(String generalGroupName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TimetableDTO timetable = getGeneralGroupSchedule(generalGroupName);
        String timeTableAsJson = mapper.writeValueAsString(timetable);

        // Regex pattern for group codes like K01, P03, L04, etc.
        String regex = "\\b[KPL]0[0-9]\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(timeTableAsJson);

        Set<String> matchedGroups = new HashSet<>();

        while (matcher.find())
            matchedGroups.add(matcher.group());

        List<String> result = new ArrayList<>(matchedGroups.stream().toList());
        Collections.sort(result);

        return result;
    }

    /**
     * Fetches and parses the full timetable for a general group.
     * @param generalGroupName group to fetch
     * @return parsed timetable
     * @throws WebPageContentNotAvailableException if remote content is unavailable
     */
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

    /**
     * Retrieves timetable and filters entries based on subgroup parameters (k, l, p).
     * @param generalGroupName name of the general group
     * @param k subgroup K code
     * @param l subgroup L code
     * @param p subgroup P code
     * @return filtered timetable
     * @throws WebPageContentNotAvailableException if source data can't be retrieved
     */
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

    /**
     * Retrieves the standard list of hour ranges used in the timetable.
     * @return list of hour labels (e.g., 08:00–09:30)
     * @throws WebPageContentNotAvailableException if hour definition page can't be loaded
     */
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
