package org.pkwmtt.timetable;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.timetable.parser.ParserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/pkmwtt/api/v1/timetable")
@RequiredArgsConstructor
public class TimeTableController {
    private final ParserService parserService;

//    @GetMapping("/data")
//    public ResponseEntity<String> getParsedData() {
//        return ResponseEntity.ok(parserService.parse("<h1>Hello</h1>"));
//    }
}
