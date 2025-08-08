package org.pkwmtt.timetable;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.pkwmtt.timetable.parser.TimetableParserService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "timetables")
public class TimetableCacheService {
    private final TimetableParserService parser;

    /**
     * Fetches and parses the full timetable for a general group.
     *
     * @param generalGroupName group to fetch
     * @return parsed timetable
     * @throws WebPageContentNotAvailableException if remote content is unavailable
     */
    @Cacheable(key = "#generalGroupName")
    public TimetableDTO getGeneralGroupSchedule(String generalGroupName) throws WebPageContentNotAvailableException, SpecifiedGeneralGroupDoesntExistsException {
        var generalGroupList = getGeneralGroupsList();

        if (!generalGroupList.containsKey(generalGroupName)){
            throw new SpecifiedGeneralGroupDoesntExistsException();
        }

        Document document;
        String url = generalGroupList.get(generalGroupName);
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
     * Retrieves a mapping of general group names to their corresponding timetable URLs.
     *
     * @return map of group names to URLs
     * @throws WebPageContentNotAvailableException if the source page can't be fetched
     */
    @Cacheable(key = "'generalGroupList'")
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
     * Retrieves the standard list of hour ranges used in the timetable.
     *
     * @return list of hour labels (e.g., 08:00–09:30)
     * @throws WebPageContentNotAvailableException if hour definition page can't be loaded
     */
    @Cacheable(key = "'hoursList'")
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
