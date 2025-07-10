package org.pkwmtt.exam;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.exam.dto.ExamDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/pkwmtt/api/v1")
@RestController
public class ExamController {

    private final ExamService examService;

    @PostMapping("/exams")
    public ResponseEntity<Void> addExam(@RequestBody ExamDto examDto){
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.created().build();
    }

    @PutMapping("/exams")
    public ResponseEntity<Void> modifyExam(@RequestBody ExamEntity exam){
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable long id){
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.noContent().build();
    }

    @GetMapping("/singleExam/{id}")
    public ResponseEntity<ExamEntity> getExam(@PathVariable long id){
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.ok();
    }

//    groups format {12K2K04P04L04}
    @GetMapping("/exams/{groups}")
    public ResponseEntity<List<ExamEntity>> getExams(@PathVariable String groups){
        throw new UnsupportedOperationException("Not supported yet.");
//        return ResponseEntity.ok();
    }

}