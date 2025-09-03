package org.pkwmtt.otp;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.GeneralGroup;
import org.pkwmtt.examCalendar.entity.OTPCode;
import org.pkwmtt.examCalendar.repository.GeneralGroupRepository;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.exceptions.MailCouldNotBeSendException;
import org.pkwmtt.exceptions.OTPCodeNotFoundException;
import org.pkwmtt.exceptions.UserNotFoundException;
import org.pkwmtt.exceptions.WrongOTPFormatException;
import org.pkwmtt.mail.EmailService;
import org.pkwmtt.mail.dto.MailDTO;
import org.pkwmtt.otp.dto.OTPRequest;
import org.pkwmtt.otp.repository.OTPCodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OTPService {
    private final OTPCodeRepository repository;
    private final UserRepository userRepository;
    private final GeneralGroupRepository generalGroupRepository;
    private final EmailService emailService;
    
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
    
    public String generateTokenForRepresentative (String code)
      throws OTPCodeNotFoundException, WrongOTPFormatException, UserNotFoundException {
        var generalGroup = this.getGeneralGroupAssignedToCode(code);
        
        var user = userRepository
          .findByGeneralGroup(generalGroup)
          .orElseThrow(() -> new UserNotFoundException("No user is assigned to this code."));
        
        var userEmail = user.getEmail();
        
        //TODO DELETE
        String token = "example-token_" + generalGroup.getName();
        
        //TODO here generate token with provided credentials
        
        //Delete used code
        repository.deleteByCode(code);
        return token;
    }
    
    public void sendOTPCodes (List<OTPRequest> requests) throws MailCouldNotBeSendException {
        requests.forEach(request -> {
            var code = generateNewCode();
            var mail = createMail(request, code);
            
            try {
                emailService.send(mail);
            } catch (MessagingException e) {
                throw new MailCouldNotBeSendException("Couldn't send mail for group: " + request.getGeneralGroupName());
            }
            
            var generalGroup = generalGroupRepository.findByName(request.getGeneralGroupName());
            
            if (generalGroup.isEmpty()) {
                generalGroup = Optional.of(generalGroupRepository.save(new GeneralGroup(null, request.getGeneralGroupName())));
            }
            
            repository.save(new OTPCode(code, generalGroup.get()));
        });
    }
    
    private MailDTO createMail (OTPRequest request, String code) {
        return new MailDTO()
          .setTitle("Kod Starosty " + request.getGeneralGroupName())
          .setRecipient(request.getEmail())
          .setDescription(request.getMailMessage(code));
    }
    
    private String generateNewCode () {
        String availableCharacters = "ABCDEFGHIJKLMNOPQRSTUWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        
        do {
            code.setLength(0);
            for (int i = 0; i < 6; i++) {
                code.append(availableCharacters.charAt(random.nextInt(availableCharacters.length())));
            }
        } while (repository.findByCode(code.toString()).isPresent());
        
        return code.toString();
    }
    
    
}
