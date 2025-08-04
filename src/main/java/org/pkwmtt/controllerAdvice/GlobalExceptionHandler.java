package org.pkwmtt.controllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pkwmtt.exceptions.ErrorResponseDTO;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(WebPageContentNotAvailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponseDTO> handleWebPageContentNotAvailableException(WebPageContentNotAvailableException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleJsonProcessingException() {
        return new ResponseEntity<>(new ErrorResponseDTO("Json Processing Failed"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SpecifiedGeneralGroupDoesntExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleSpecifiedGeneralGroupDoesntExistsException(SpecifiedGeneralGroupDoesntExistsException e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
