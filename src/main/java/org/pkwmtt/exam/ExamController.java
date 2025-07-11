package org.pkwmtt.exam;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.exam.dto.ExamDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/pkwmtt/api/v1/exams")
@RestController
public class ExamController {

    private final ExamService examService;

    /**
     * @param exam details of exam
     * @return
     */
    @PostMapping("")
    public ResponseEntity<Void> addExam(@RequestBody ExamDto exam) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.created().build();
    }

    /**
     * @param id of exam or test
     * @param exam new details of exam or test
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyExam(@PathVariable long id, @RequestBody ExamEntity exam) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.noContent().build();
    }

    /**
     * @param id of exam or test
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable long id) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.noContent().build();
    }

    /**
     * @param id of exam or test
     * @return single exam or test details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamEntity> getExam(@PathVariable long id) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.ok();
    }

    /**
     * @param generalGroup symbol that identify exercise group of specific field of study (for example 12K2)
     * @param k computer laboratory group
     * @param l laboratory group
     * @param p project group
     * @return
     */
    @GetMapping("/by-groups/{generalGroup}")
    public ResponseEntity<List<ExamEntity>> getExams(
            @PathVariable String generalGroup,
            @RequestParam(name = "k", required = false) String k,
            @RequestParam(name = "l", required = false) String l,
            @RequestParam(name = "p", required = false) String p
            ) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.ok();
    }

}