package org.pkwmtt.otp;


import org.pkwmtt.exceptions.MailCouldNotBeSendException;
import org.pkwmtt.exceptions.OTPCodeNotFoundException;
import org.pkwmtt.exceptions.UserNotFoundException;
import org.pkwmtt.exceptions.WrongOTPFormatException;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {OTPController.class})
public class OTPExceptionHandler {
    @ExceptionHandler({OTPCodeNotFoundException.class, WrongOTPFormatException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequests (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({MailCouldNotBeSendException.class})
    public ResponseEntity<ErrorResponseDTO> handleServerErrors (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
