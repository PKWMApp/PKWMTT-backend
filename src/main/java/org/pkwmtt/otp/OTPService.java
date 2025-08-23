package org.pkwmtt.otp;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.GeneralGroup;
import org.pkwmtt.examCalendar.entity.OTPCode;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.exceptions.OTPCodeNotFoundException;
import org.pkwmtt.exceptions.UserNotFoundException;
import org.pkwmtt.exceptions.WrongOTPFormatException;
import org.pkwmtt.otp.repository.OTPCodeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OTPService {
    private final OTPCodeRepository repository;
    private final UserRepository userRepository;
    
    private GeneralGroup getGeneralGroupAssignedToCode (String code) throws OTPCodeNotFoundException, WrongOTPFormatException {
        this.validateCode(code);
        
        Optional<OTPCode> result = repository.findByCode(code);
        
        if (result.isEmpty()) {
            throw new OTPCodeNotFoundException();
        }
        
        return result.get().getGeneralGroup();
    }
    
    private void validateCode (String code) throws WrongOTPFormatException {
        if (code.length() != 6) {
            throw new WrongOTPFormatException("Code should be 6 characters long.");
        }
        
        String regex = "^[A-Z0-9]{6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);
        
        if (!matcher.find()) {
            throw new WrongOTPFormatException("Wrong format of provided code.");
        }
        
    }
    
    public String generateTokenForRepresentative (String code) throws OTPCodeNotFoundException, WrongOTPFormatException, UserNotFoundException {
        var generalGroup = this.getGeneralGroupAssignedToCode(code);
        if (userRepository.findByGeneralGroup(generalGroup).isEmpty()) {
            throw new UserNotFoundException("No user is assigned to this code.");
        }
        var userEmail = userRepository.findByGeneralGroup(generalGroup).get().getEmail();
        
        String token = "example-token";
        
        //TODO here generate token with provided credentials
        
        return token;
    }
}
