package org.pkwmtt.files;

import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.pkwmtt.files.apk.ApkController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice(assignableTypes = {FileController.class, ApkController.class})
public class FileUploadsExceptionHandler {
    
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseDTO> handleIOException () {
        return new ResponseEntity<>(
          new ErrorResponseDTO("File or directory not found or is malformed."),
                                    HttpStatus.NOT_FOUND
        );
    }
    
    @ExceptionHandler({IllegalAccessException.class, RuntimeException.class})
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
