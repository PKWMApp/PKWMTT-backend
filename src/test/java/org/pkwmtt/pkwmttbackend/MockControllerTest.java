package org.pkwmtt.pkwmttbackend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;


/**
 * Example Test Class
 */
@ExtendWith(SpringExtension.class)
//Assigns random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockControllerTest {

    //Gets port that test works on
    @LocalServerPort
    private int port;

    //Object used to fetch url
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Example test for GET method
     */
    @Test
    public void getHello() {
        //Make GET request on specified URL
        ResponseEntity<String> response = restTemplate.getForEntity(String.format("http://localhost:%s/api/v1/hello", port), String.class);

        //Check status
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        //Check body
        Assertions.assertEquals("Hello", response.getBody());
    }
}
