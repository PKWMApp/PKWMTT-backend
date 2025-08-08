package org.pkwmtt.temp;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.cache.CacheInspector;
import org.pkwmtt.timetable.TimetableCacheService;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheInspectorController {
    private final TimetableService service;
    private final TimetableCacheService cacheableTimetableService;
    private final CacheInspector cacheInspector;

    @GetMapping
    public String temp() {
        List<String> generalGroups = cacheableTimetableService.getGeneralGroupsList().keySet().stream().toList();
        StringBuilder stringBuilder = new StringBuilder();

        generalGroups.forEach(group -> {
            try {
                stringBuilder.append(group).append(": ");
                stringBuilder.append(service.getAvailableSubGroups(group).isEmpty() ? "\t\tBAD" : "\tGOOD");
                stringBuilder.append("\n");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return String.format(
            "%s\n\n%s",
            stringBuilder,
            cacheInspector.printAllEntries("timetables")
        );
    }
}
