package org.pkwmtt.pkwmttbackend;

import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pkwmtt.MockController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Example Test Class
 */
@WebMvcTest(MockController.class)
@Import(SecurityConfig.class)
public class MockControllerTest {

    //Simulating HTTP requests
    @Autowired
    private MockMvc mockMvc;

    /**
     * Example test for GET method
     * Tests /api/v1/hello endpoint
     */
    @WithMockUser
    @Test
    public void getHello() throws Exception {
        mockMvc.perform(get("/api/v1/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello"));
    }
}
