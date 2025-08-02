package org.pkwmtt.examCalendar;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.dto.ExamDto;
import org.pkwmtt.examCalendar.entity.Exam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<Void> addExam(@RequestBody ExamDto examDto) {
        int id = examService.addExam(examDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).build();
//        TODO: add data verification
    }

    /**
     * @param id of exam or test
     * @param examDto new details of exam or test
     * @return 204 no content
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyExam(@PathVariable int id, @RequestBody ExamDto examDto) {
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
     * @param generalGroup symbol that identify exercise group of specific field of study (for example 12K2)
     * @param k computer laboratory group (non required)
     * @param l laboratory group (non required)
     * @param p project group (non required)
     * @return 200 ok with list of exams for specific group
     */
    @GetMapping("/by-groups/{generalGroup}")
    public ResponseEntity<Set<Exam>> getExams(
            @PathVariable String generalGroup,
            @RequestParam(name = "k", required = false) String k,
            @RequestParam(name = "l", required = false) String l,
            @RequestParam(name = "p", required = false) String p
            ){
        return ResponseEntity.ok(examService.getExamByGroup(generalGroup, k, l, p));
    }

}