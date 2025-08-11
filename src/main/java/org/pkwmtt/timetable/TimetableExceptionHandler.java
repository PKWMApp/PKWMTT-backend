package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SuppressWarnings({"LoggingSimilarMessage", "StringConcatenationArgumentToLogCall"})
@Slf4j
@RestControllerAdvice(assignableTypes = {TimetableController.class})
public class TimetableExceptionHandler {
    @ExceptionHandler(WebPageContentNotAvailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponseDTO> handleWebPageContentNotAvailableException (WebPageContentNotAvailableException e) {
        log.error("SERVICE_UNAVAILABLE # " + e.getMessage());
        return new ResponseEntity<>(
          new ErrorResponseDTO(e.getMessage()),
          HttpStatus.SERVICE_UNAVAILABLE
        );
    }
    
    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleJsonProcessingException (JsonProcessingException e) {
        log.error("INTERNAL_SERVER_ERROR # " + e.getMessage());
        return new ResponseEntity<>(
          new ErrorResponseDTO("Json Processing Failed"),
          HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    @ExceptionHandler({SpecifiedGeneralGroupDoesntExistsException.class, SpecifiedSubGroupDoesntExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleSpecifiedGeneralGroupDoesntExistsException (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleIllegalAccessException (IllegalAccessException e) {
        log.error("INTERNAL_SERVER_ERROR # " + e.getMessage());
        return new ResponseEntity<>(
          new ErrorResponseDTO(e.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
