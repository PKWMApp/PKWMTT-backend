package org.pkwmtt.cache;

import org.junit.jupiter.api.Test;
import org.pkwmtt.timetable.TimetableCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CacheConfigTest {
    @Autowired
    private TimetableCacheService service;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCacheKeyPresent_Schedule() {
        service.getGeneralGroupSchedule("12K1");

        Cache cache = cacheManager.getCache("timetables");
        assertThat(cache).isNotNull();

        Cache.ValueWrapper wrapper = cache.get("timetable_12K1");
        assertThat(wrapper).isNotNull();
        assertThat(wrapper.get()).isInstanceOf(String.class);

    }

    @Test
    void testCacheKeyPresent_HoursList(){
        service.getListOfHours();

        Cache cache = cacheManager.getCache("timetables");
        assertThat(cache).isNotNull();

        Cache.ValueWrapper wrapper = cache.get("hourList");
        assertThat(wrapper).isNotNull();
        assertThat(wrapper.get()).isInstanceOf(String.class);
    }

}