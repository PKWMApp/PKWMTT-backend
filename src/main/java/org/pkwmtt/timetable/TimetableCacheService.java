package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.pkwmtt.timetable.parser.TimetableParserService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class TimetableCacheService {
    private final TimetableParserService parser;
    private final ObjectMapper mapper;

    private final Cache cache;

    public TimetableCacheService(TimetableParserService parser, ObjectMapper mapper, CacheManager cacheManager) throws
                                                                                                                IllegalAccessException {
        this.parser = parser;
        this.mapper = mapper;
        cache = cacheManager.getCache("timetables");

        if (isNull(cache)) {
            throw new IllegalAccessException("Cache [timetables] not configured");
        }
    }

    /**
     * Fetches and parses the full timetable for a general group.
     *
     * @param generalGroupName group to fetch
     * @return parsed timetable
     * @throws WebPageContentNotAvailableException if remote content is unavailable
     */
    public TimetableDTO getGeneralGroupSchedule(String generalGroupName) throws WebPageContentNotAvailableException,
                                                                                SpecifiedGeneralGroupDoesntExistsException {
        var generalGroupList = getGeneralGroupsMap();

        if (!generalGroupList.containsKey(generalGroupName)) {
            throw new SpecifiedGeneralGroupDoesntExistsException();
        }

        String groupUrl = generalGroupList.get(generalGroupName);
        String url = String.format("https://podzial.mech.pk.edu.pl/stacjonarne/html/%s", groupUrl);
        String cacheKey = "timetable_" + generalGroupName;
        String json = cache.get(
            cacheKey,
            () -> mapper.writeValueAsString(new TimetableDTO(generalGroupName, parser.parse(fetchData(url))))
        );

        return getMappedValue(
            json, cacheKey, cache, new TypeReference<>() {
            }
        );
    }

    /**
     * Retrieves a mapping of general group names to their corresponding timetable URLs.
     *
     * @return map of group names to URLs
     * @throws WebPageContentNotAvailableException if the source page can't be fetched
     */
    public Map<String, String> getGeneralGroupsMap() throws WebPageContentNotAvailableException {
        String url = "http://podzial.mech.pk.edu.pl/stacjonarne/html/lista.html";
        String json = cache.get(
            "generalGroupList",
            () -> mapper.writeValueAsString(parser.parseGeneralGroups(fetchData(url)))
        );
        return getMappedValue(
            json, "generalGroupList", cache, new TypeReference<>() {
            }
        );

    }

    public List<String> getGeneralGroupsList() {
        return getGeneralGroupsMap().keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }


    /**
     * Retrieves the standard list of hour ranges used in the timetable.
     *
     * @return list of hour labels (e.g., 08:00–09:30)
     * @throws WebPageContentNotAvailableException if hour definition page can't be loaded
     */
    public List<String> getListOfHours() throws WebPageContentNotAvailableException {
        String url = "https://podzial.mech.pk.edu.pl/stacjonarne/html/plany/o25.html";

        String json = cache.get("hourList", () -> mapper.writeValueAsString(parser.parseHours(fetchData(url))));

        return getMappedValue(
            json, "hourList", cache, new TypeReference<>() {
            }
        );
    }

    private <T> T getMappedValue(String json, String key, Cache cache, TypeReference<T> targetClass) throws
                                                                                                     WebPageContentNotAvailableException {
        try {
            return mapper.readValue(json, targetClass);
        } catch (JsonProcessingException e) {
            cache.evict(key);
            throw new WebPageContentNotAvailableException();
        }
    }

    private String fetchData(String url) throws WebPageContentNotAvailableException {
        try {
            return Jsoup.connect(url).get().html();
        } catch (IOException ioe) {
            throw new WebPageContentNotAvailableException();
        }
    }

}
