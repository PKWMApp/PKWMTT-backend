package org.pkwmtt.examCalendar;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.pkwmtt.exceptions.ErrorResponseDTO;
import org.pkwmtt.exceptions.ExamTypeNotExistsException;
import org.pkwmtt.exceptions.NoSuchElementWithProvidedIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExamControllerAdvice {

//  TODO: handle or remove UnsupportedCountOfArgumentsException

    @ExceptionHandler(NoSuchElementWithProvidedIdException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoSuchElementWithProvidedIdException(NoSuchElementWithProvidedIdException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(ExamTypeNotExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleExamTypeNotExistsException(ExamTypeNotExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(field -> field.getField() + " : " + field.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(field -> field.getPropertyPath() + " : " + field.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(message));
    }


}
