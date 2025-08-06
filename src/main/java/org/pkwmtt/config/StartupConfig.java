package org.pkwmtt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * Logs server base url so you can click on it after start and
 * go directly to swagger
 */
@Slf4j
@Component
public class StartupConfig {

    @Value("${server.port:}")
    String port = "";

    @Value("${server.address:}")
    String address = "";

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        log.error("TEST");
        try {
            if (port.isEmpty() || address.isEmpty())
                throw new Exception();
            log.info("SERVER URL: http://{}:{}", address, port);
        } catch (Exception e) {
            log.error("!Couldn't log the server base url. Check properties in application.properties");
        }
    }

}
