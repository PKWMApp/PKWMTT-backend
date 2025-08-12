package org.pkwmtt.cache;

import org.junit.jupiter.api.Test;
import org.pkwmtt.ValuesForTest;
import org.pkwmtt.timetable.TimetableCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock({
        @ConfigureWireMock(
                name = "my-mock",
                port = 8888)
})
class CacheConfigTest {
    @Autowired
    private TimetableCacheService service;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCacheKeyPresent_Schedule() {
        //given
        initWireMock();

        //when
        service.getGeneralGroupSchedule("12K1");
        var cache = cacheManager.getCache("timetables");

        //then
        assertAll(
                () -> {
                    assertThat(cache).isNotNull();
                    assertThat(cache.get("generalGroupMap", String.class))
                            .isEqualTo("{\"11K2\":\"plany/o8.html\",\"12K1\":\"plany/o25.html\",\"11A1\":\"plany/o1.html\",\"12K3\":\"plany/o27.html\",\"12K2\":\"plany/o26.html\"}");
                },
                () -> {
                    var wrapper = cache.get("timetable_12K1");
                    assertThat(wrapper).isNotNull();
                    assertThat(wrapper.get()).isInstanceOf(String.class);
                }
        );
    }

    @Test
    void testCacheKeyPresent_HoursList(){
        service.getListOfHours();

        var cache = cacheManager.getCache("timetables");
        assertThat(cache).isNotNull();

        Cache.ValueWrapper wrapper = cache.get("hourList");
        assertThat(wrapper).isNotNull();
        assertThat(wrapper.get()).isInstanceOf(String.class);
    }

    private void initWireMock() {
        stubFor(get(urlPathMatching("/plany/o25.html"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/*")
                        .withBody(ValuesForTest.timetableHTML)));

        stubFor(get(urlPathMatching("/lista.html"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/*")
                        .withBody(ValuesForTest.listHTML)));
    }

}