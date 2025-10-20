package org.pkwmtt.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.timetable.TimetableCacheService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task that evicts configured caches every day at midnight.
 * By default this uses the server's local timezone; if you need a specific timezone
 * set the "zone" attribute on the @Scheduled annotation (for example zone = "UTC").
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledCache {
    private final CacheManager cacheManager;
    private final TimetableCacheService cacheService;
    
    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Warsaw")
    public void evictAllCachesAtMidnight () {
        log.info("Scheduled cache eviction triggered - clearing caches");
        for (String name : cacheManager.getCacheNames()) {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
                log.debug("Cleared cache '{}'", name);
            }
        }
    }
    
    @Scheduled(cron = "0 0 1 * * *", zone = "Europe/Warsaw")
    public void prepopulateGeneralGroupCachesAtOneAM () throws JsonProcessingException {
        log.info("Prepopulating general groups caches at 01:00 - saving timetables to caches");
        prepopulateGeneralGroups();
    }
    
    private void prepopulateGeneralGroups () throws JsonProcessingException {
        for (var generalGroup : cacheService.getGeneralGroupsMap().keySet()) {
            try {
                cacheService.getGeneralGroupSchedule(generalGroup);
                log.debug("Prepopulated timetable cache for general group '{}'", generalGroup);
            } catch (Exception ex) {
                log.warn("Failed to prepopulate timetable cache for general group '{}'", generalGroup, ex);
            }
        }
    }
    
    
}

