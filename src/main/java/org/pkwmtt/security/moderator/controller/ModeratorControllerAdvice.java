package org.pkwmtt.security.moderator.controller;

import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = ModeratorController.class)
public class ModeratorControllerAdvice {

    @ExceptionHandler(SpecifiedGeneralGroupDoesntExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(SpecifiedGeneralGroupDoesntExistsException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(e.getMessage()));
    }

}
