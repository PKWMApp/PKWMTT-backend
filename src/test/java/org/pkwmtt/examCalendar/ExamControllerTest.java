package org.pkwmtt.examCalendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.pkwmtt.examCalendar.repository.ExamTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * integration tests of ExamCalendar
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExamTypeRepository examTypeRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setupBeforeEach() {
        examRepository.deleteAll();
        examTypeRepository.deleteAll();
        examTypeRepository.save(ExamType.builder().name("Project").build());
    }


//<editor-fold desc="addExam">
    /**
     * check if addExam endpoint create new exam with correct URI and correct data
     */
    @Test
    void addExamWithCorrectData() throws Exception {
        ExamDto examDtoRequest = new ExamDto(
                "Math exam",
                "first exam",
                LocalDateTime.now().plusDays(1),
                "12K2, L04",
                "Project"
        );

        String json = mapper.writeValueAsString(examDtoRequest);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(json)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/pkwmtt/api/v1/exams/")))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        @SuppressWarnings("DataFlowIssue")
        int id = Integer.parseInt(location.substring(location.lastIndexOf("/") + 1));

        Exam examResponse = examRepository.findById(id).orElseThrow();

        assertEquals(examDtoRequest.getTitle(), examResponse.getTitle());
        assertEquals(examDtoRequest.getDescription(), examResponse.getDescription());
//        compare dates with minutes level precision
        assertEquals(
                examDtoRequest.getDate().truncatedTo(ChronoUnit.MINUTES),
                examResponse.getDate().truncatedTo(ChronoUnit.MINUTES)
        );
        assertEquals(examDtoRequest.getExamGroups(), examResponse.getExamGroups());
        assertEquals(examDtoRequest.getExamType(), examResponse.getExamType().getName());
    }

    @Test
    void addExamWithBlankExamTitle() throws Exception {
        Map<String, String> requestData = new HashMap<>();
//      no exam title
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("title : must not be blank", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithBlankExamDescription() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
//        no exam description
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        @SuppressWarnings("DataFlowIssue")
        int id = Integer.parseInt(location.substring(location.lastIndexOf("/") + 1));

        Exam examResponse = examRepository.findById(id).orElseThrow();

        assertNull(examResponse.getDescription());
    }

    @Test
    void addExamWithBlankDate() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
//      no date
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("date : must not be null", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithBlankExamGroups() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
//        no examGroups
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("examGroups : must not be blank", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithNullExamTypes() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
//      no examType

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("examType : must not be null", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithNotFutureDate() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().minusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("date : Date must be in the future", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithEmptyStringExamTitle() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("title : must not be blank", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithEmptyStringExamGroups() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "");
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("examGroups : must not be blank", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithTooLongExamTitle() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("title : max size of field is 255", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithTooLongDescription() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("description : max size of field is 255", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithTooLongExamGroups() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        requestData.put("examType", "Project");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("examGroups : max size of field is 255", jsonResponse.get("message").asText());
    }

    @Test
    void addExamWithNonExistingExamType() throws Exception {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "NonExistingExamType");

        String jsonRequest = mapper.writeValueAsString(requestData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(jsonRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals("Invalid exam type NonExistingExamType", jsonResponse.get("message").asText());
    }



    //</editor-fold>

    @Test
    void modifyExam() {
    }

    @Test
    void deleteExam() {
    }

    @Test
    void getExam() {
    }

    @Test
    void getExams() {
    }

    @Test
    void getExamTypes() {
    }
}