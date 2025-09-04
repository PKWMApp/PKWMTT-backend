package org.pkwmtt.otp;


import com.mysql.cj.exceptions.WrongArgumentException;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {OTPController.class})
public class OTPExceptionHandler {
    @ExceptionHandler({OTPCodeNotFoundException.class, WrongOTPFormatException.class, UserNotFoundException.class, WrongArgumentException.class, SpecifiedGeneralGroupDoesntExistsException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequests (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({MailCouldNotBeSendException.class})
    public ResponseEntity<ErrorResponseDTO> handleServerErrors (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
