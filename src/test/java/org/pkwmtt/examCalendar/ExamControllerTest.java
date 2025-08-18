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
import org.springframework.test.web.servlet.ResultMatcher;
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
    }

    //<editor-fold desc="addExam">

    /**
     * check if addExam endpoint create new exam with correct URI and correct data
     */
    @Test
    void addExamWithCorrectData() throws Exception {
//        given
        createExampleExamType("Project");
        ExamDto examDtoRequest = createExampleExamDto("Project");
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
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
//      no exam title
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("title : must not be blank", result);
    }

    @Test
    void addExamWithBlankExamDescription() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
//        no exam description
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isCreated(), requestData);

        String location = result.getResponse().getHeader("Location");
        @SuppressWarnings("DataFlowIssue")
        int id = Integer.parseInt(location.substring(location.lastIndexOf("/") + 1));

        Exam examResponse = examRepository.findById(id).orElseThrow();
        assertNull(examResponse.getDescription());
    }

    @Test
    void addExamWithBlankDate() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
//      no date
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("date : must not be null", result);
    }

    @Test
    void addExamWithBlankExamGroups() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
//        no examGroups
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("examGroups : must not be blank", result);
    }

    @Test
    void addExamWithNullExamTypes() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
//      no examType

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("examType : must not be null", result);
    }

    @Test
    void addExamWithNotFutureDate() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().minusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("date : Date must be in the future", result);
    }

    @Test
    void addExamWithEmptyStringExamTitle() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("title : must not be blank", result);
    }

    @Test
    void addExamWithEmptyStringExamGroups() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "");
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("examGroups : must not be blank", result);
    }

    @Test
    void addExamWithTooLongExamTitle() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("title : max size of field is 255", result);
    }

    @Test
    void addExamWithTooLongDescription() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("description : max size of field is 255", result);
    }

    @Test
    void addExamWithTooLongExamGroups() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        requestData.put("examType", "Project");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("examGroups : max size of field is 255", result);
    }

    @Test
    void addExamWithNonExistingExamType() throws Exception {
//        given
        createExampleExamType("Project");
        Map<String, String> requestData = new HashMap<>();
        requestData.put("title", "Math exam");
        requestData.put("description", "first exam");
        requestData.put("date", LocalDateTime.now().plusDays(1).toString());
        requestData.put("examGroups", "12K2, L04");
        requestData.put("examType", "NonExistingExamType");

//        when
        MvcResult result = assertPostRequest(status().isBadRequest(), requestData);

//        then
        assertResponseMessage("Invalid exam type NonExistingExamType", result);
    }


    //</editor-fold>

    //    <editor-fold desc="modifyExam">
    @Test
    void modifyExamWithCorrectData() throws Exception {
//        given
        ExamType examType = createExampleExamType("Exam");
        Exam exam = createExampleExam(examType);
        int id = examRepository.save(exam).getExamId();
        ExamDto examDto = createExampleExamDto(examType.getName());

//        when
        assertPutRequest(status().isNoContent(), examDto, id);

//        then
        Exam responseExam = examRepository.findById(id).orElseThrow();
        assertEquals("Math exam", responseExam.getTitle());
        assertEquals("first exam", responseExam.getDescription());
        assertEquals(
                LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES),
                responseExam.getDate().truncatedTo(ChronoUnit.MINUTES)
        );
        assertEquals("12K2, L04", responseExam.getExamGroups());
    }

    @Test
    void modifyExamWithIncorrectExamId() throws Exception {
//        given
        ExamType examType = createExampleExamType("Exam");
        Exam exam = createExampleExam(examType);
        int id = examRepository.save(exam).getExamId();
        ExamDto examDto = createExampleExamDto(examType.getName());

        int invalidId = Integer.MAX_VALUE - 10;
        assertNotEquals(invalidId, id);
//        when
        MvcResult result = assertPutRequest(status().isNotFound(), examDto, invalidId);

//        then
        assertResponseMessage("No such element with id: " + (invalidId), result);

    }
//    </editor-fold>

    //    <editor-fold desc="deleteExam">
    @Test
    void deleteExamWithCorrectArguments() throws Exception {
//        given
        ExamType examType = createExampleExamType("Exam");
        Exam exam = createExampleExam(examType);
        int id = examRepository.save(exam).getExamId();

//        when
        assertDeleteRequest(status().isNoContent(), id);

//        then
        assertTrue(examRepository.findById(id).isEmpty());
    }

    @Test
    void deleteNonExistingExam() throws Exception {
//        given
        ExamType examType = createExampleExamType("Exam");
        Exam exam = createExampleExam(examType);
        int id = examRepository.save(exam).getExamId();
        int invalidId = Integer.MAX_VALUE - 10;
        assertNotEquals(invalidId, id);

//        when
        MvcResult result = assertDeleteRequest(status().isNotFound(), invalidId);

//        then
        assertTrue(examRepository.findById(id).isPresent());
        assertResponseMessage("No such element with id: " + (invalidId), result);
    }

    //    </editor-fold>

    //    <editor-fold desc="getExamById">

    @Test
    void getExamByIdWithCorrectId() throws Exception {
//        given
        ExamType examType = createExampleExamType("Exam");
        Exam exam = createExampleExam(examType);
        int id = examRepository.save(exam).getExamId();

//        when
        MvcResult result = assertGetByIdRequest(status().isOk(), id);
        JsonNode responseNode = mapper.readTree(result.getResponse().getContentAsString());

//        then
        assertEquals(exam.getTitle(), responseNode.get("title").asText());
        assertEquals(exam.getDescription(), responseNode.get("description").asText());
        assertEquals(
                exam.getDate().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.parse(responseNode.get("date").textValue()).truncatedTo(ChronoUnit.MINUTES)
        );
        assertEquals(exam.getExamGroups(), responseNode.get("examGroups").asText());
        assertEquals(mapper.readTree(mapper.writeValueAsString(exam.getExamType())), responseNode.get("examType"));
    }

    @Test
    void getNonExistingExamById() throws Exception {
//        given
        ExamType examType = createExampleExamType("Exam");
        Exam exam = createExampleExam(examType);
        int id = examRepository.save(exam).getExamId();
        int invalidId = Integer.MAX_VALUE - 10;
        assertNotEquals(invalidId, id);

//        when
        MvcResult result = assertGetByIdRequest(status().isNotFound(), invalidId);

//        then
        assertResponseMessage("No such element with id: " + (invalidId), result);
    }

//  </editor-fold>

    @Test
    void getExams() {
//        TODO: test getExamsByGroups after implementing new version
    }

    //    <editor-fold desc="getExamTypes">

    @Test
    void getExamTypesWhenExamTypesExists() throws Exception {
//        given
        ExamType exam = createExampleExamType("Exam");
        ExamType project = createExampleExamType("Project");

//        when
        MvcResult result = assertGetExamTypesRequest(status().isOk());
        JsonNode responseArray = mapper.readTree(result.getResponse().getContentAsString());

//        then
        assertEquals(2, responseArray.size());
        assertTrue(responseArray.valueStream().anyMatch(e -> e.get("name").asText().equals(exam.getName())));
        assertTrue(responseArray.valueStream().anyMatch(e -> e.get("name").asText().equals(project.getName())));
    }

    @Test
    void getExamTypesWhenExamTypesNotExists() throws Exception {
//        given
//        when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/pkwmtt/api/v1/exams/exam-types")
                        .contentType("application/json")
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        JsonNode responseArray = mapper.readTree(result.getResponse().getContentAsString());

//        then
        assertEquals(0, responseArray.size());
    }

    //    </editor-fold>

    //    <editor-fold desc="helper methods">

    /**
     * this method create examType object and add it to repository
     * @param name of new examType
     * @return created examType object
     */
    private ExamType createExampleExamType(String name) {
        ExamType examType = ExamType.builder().name(name).build();
        examTypeRepository.save(examType);
        return examType;
    }

    /**
     * this method don't add created Exam to repository, because in that case id of created Exam would be unreachable
     * @param type ExamType object which is required argument of Exam
     * @return created Exam
     */
    private Exam createExampleExam(ExamType type) {
        return Exam.builder()
                .title("Exam")
                .description("Exam description")
                .date(LocalDateTime.now().plusDays(1))
                .examGroups("11K1, L01")
                .examType(type)
                .build();
    }

    /**
     * @param examTypeName name of type of exam as String
     * @return created ExamDto
     */
    private ExamDto createExampleExamDto(String examTypeName) {
        return new ExamDto(
                "Math exam",
                "first exam",
                LocalDateTime.now().plusDays(1),
                "12K2, L04",
                examTypeName
        );
    }

    /**
     * compare error message form response with expected value
     * @param expectedMessage full message that is expected in response
     * @param result response generated by mockMvc.perform() or one of assert[httpMethod]Request()
     * @throws Exception
     */
    private void assertResponseMessage(String expectedMessage, MvcResult result) throws Exception {
        JsonNode jsonResponse = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonResponse.has("message"));
        assertEquals(expectedMessage, jsonResponse.get("message").asText());
    }

    /**
     * method send POST request to ExamController with content as JSON attached to body and then check if response
     * code is the same as expected
     * @param expectedStatus status().[http response] (example: status().isCreated() )
     * @param content object that would be mapped to JSON by ObjectMapper and then attached to request
     *                it could be dto object or Map<String, String>
     * @return MvcResult object which could be used to capture response body
     * @throws Exception
     */
    private MvcResult assertPostRequest(ResultMatcher expectedStatus, Object content) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                        .post("/pkwmtt/api/v1/exams")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(content))
                ).andDo(print())
                .andExpect(expectedStatus)
                .andReturn();
    }

    /**
     * method send PUT request to ExamController with content as JSON attached to body and examId as pathID.
     * Then check if response code is the same as expected
     * @param expectedStatus status().[http response] (example: status().isNoContent() )
     * @param content object that would be mapped to JSON by ObjectMapper and then attached to request
     * @param pathId id of resource that would be updated
     * @return MvcResult object which could be used to capture response body
     * @throws Exception
     */
    private MvcResult assertPutRequest(ResultMatcher expectedStatus, Object content, int pathId) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                        .put("/pkwmtt/api/v1/exams/{id}", pathId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(content))
                ).andDo(print())
                .andExpect(expectedStatus)
                .andReturn();
    }

    /**
     * method send DELETE request to ExamController with examId as pathID.
     * Then check if response code is the same as expected
     * @param expectedStatus status().[http response] (example: status().isNoContent() )
     * @param pathId id of resource that would be deleted
     * @return MvcResult object which could be used to capture response body
     * @throws Exception
     */
    private MvcResult assertDeleteRequest(ResultMatcher expectedStatus, int pathId) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                        .delete("/pkwmtt/api/v1/exams/{id}", pathId)
                        .contentType("application/json")
                ).andDo(print())
                .andExpect(expectedStatus)
                .andReturn();
    }

    /**
     * method send GET request to ExamController at /pkwmtt/api/v1/exams/{id} URI with examId as pathID.
     * Then check if response code is the same as expected
     * @param expectedStatus status().[http response] (example: status().isOk() )
     * @param pathId id of resource that would be returned
     * @return MvcResult object which could be used to capture response body
     * @throws Exception
     */
    private MvcResult assertGetByIdRequest(ResultMatcher expectedStatus, int pathId) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                        .get("/pkwmtt/api/v1/exams/{id}", pathId)
                        .contentType("application/json")
                ).andDo(print())
                .andExpect(expectedStatus)
                .andReturn();
    }

    /**
     * method send GET request to ExamController at /pkwmtt/api/v1/exams/exam-types URI.
     * Then check if response code is the same as expected
     * @param expectedStatus expectedStatus status().[http response] (example: status().isOk() )
     * @return MvcResult object which could be used to capture response body
     * @throws Exception
     */
    private MvcResult assertGetExamTypesRequest(ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                        .get("/pkwmtt/api/v1/exams/exam-types")
                        .contentType("application/json")
                ).andDo(print())
                .andExpect(expectedStatus)
                .andReturn();
    }

//    </editor-fold>

}