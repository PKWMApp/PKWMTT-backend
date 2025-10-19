package org.pkwmtt.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
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
public class ScheduledCacheEvict {
    private final CacheManager cacheManager;
    
    // Run every day at 00:00:00 server local time
    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Warsaw") // Adjust the time as needed
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
}

