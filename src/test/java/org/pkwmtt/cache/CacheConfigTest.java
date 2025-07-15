package org.pkwmtt.cache;

import org.junit.jupiter.api.Test;
import org.pkwmtt.timetable.CacheableTimetableService;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CacheConfigTest {
    @Autowired
    private CacheableTimetableService service;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCacheKeyPresent_Schedule() {
        service.getGeneralGroupSchedule("12K1");

        Cache cache = cacheManager.getCache("timetables");
        assertThat(cache).isNotNull();

        Cache.ValueWrapper wrapper = cache.get("12K1");
        assertThat(wrapper).isNotNull();
        assertThat(wrapper.get()).isInstanceOf(TimetableDTO.class);

        TimetableDTO second = service.getGeneralGroupSchedule("12K1");
        assertThat(second).isSameAs(wrapper.get());
    }

    @Test
    void testCacheKeyPresent_HoursList(){
        service.getListOfHours();

        Cache cache = cacheManager.getCache("timetables");
        assertThat(cache).isNotNull();

        Cache.ValueWrapper wrapper = cache.get("hoursList");
        assertThat(wrapper).isNotNull();
        assertThat(wrapper.get()).isInstanceOf(ArrayList.class);

        List<String> second = service.getListOfHours();
        assertThat(second).isSameAs(wrapper.get());
    }

}