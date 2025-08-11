package org.pkwmtt.examCalendar;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.pkwmtt.examCalendar.entity.ExamType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("/pkwmtt/api/v1/exams")
@RestController
public class ExamController {

    private final ExamService examService;

    /**
     * @param examDto details of exam
     * @return 201 created with URI to GET method which returns created resource
     */
    @PostMapping("")
    public ResponseEntity<Void> addExam(@RequestBody @Valid ExamDto examDto) {
        int id = examService.addExam(examDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).build();
//        TODO: test not null validation in controller
    }

    /**
     * @param id of exam or test
     * @param examDto new details of exam or test
     * @return 204 no content
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyExam(@PathVariable int id,@RequestBody @Valid ExamDto examDto) {
        examService.modifyExam(examDto, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id of exam or test
     * @return 204 no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable int id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id of exam or test
     * @return 200 ok with single exam or test details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExam(@PathVariable int id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    /**
     * @param groups set of groups
     * @return 200 ok with list of exams for specific group
     */
    @GetMapping("/by-groups")
    public ResponseEntity<Set<Exam>> getExams(@RequestParam Set<String> groups){
        return ResponseEntity.ok(examService.getExamByGroup(groups));
    }

    /**
     * @return 200 ok with list of available exam types
     */
//    should be moved to new controller?
    @GetMapping("/exam-types")
    public ResponseEntity<List<ExamType>> getExamTypes(){
        return ResponseEntity.ok(examService.getExamTypes());
    }

}