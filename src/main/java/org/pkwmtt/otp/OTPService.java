package org.pkwmtt.otp;

import com.mysql.cj.exceptions.WrongArgumentException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.GeneralGroup;
import org.pkwmtt.examCalendar.entity.OTPCode;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.examCalendar.repository.GeneralGroupRepository;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.mail.EmailService;
import org.pkwmtt.mail.dto.MailDTO;
import org.pkwmtt.otp.dto.OTPRequest;
import org.pkwmtt.otp.repository.OTPCodeRepository;
import org.pkwmtt.security.token.JwtServiceImpl;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OTPService {
    private final OTPCodeRepository repository;
    private final UserRepository userRepository;
    private final GeneralGroupRepository generalGroupRepository;
    private final EmailService emailService;
    private final JwtServiceImpl jwtService;
    private final TimetableService timetableService;
    
    public String generateTokenForRepresentative (String code)
      throws OTPCodeNotFoundException, WrongOTPFormatException, UserNotFoundException {
        var generalGroup = this.getGeneralGroupAssignedToCode(code);
        var user = userRepository
          .findByGeneralGroup(generalGroup)
          .orElseThrow(() -> new UserNotFoundException("No user is assigned to this code."));
        
        var userEmail = user.getEmail();
        String token = jwtService.generateToken(new UserDTO()
                                                  .setEmail(userEmail)
                                                  .setRole(Role.REPRESENTATIVE)
                                                  .setGroup(generalGroup.getName()));
        repository.deleteByCode(code);
        return token;
    }
    
    public void sendOTPCodes (List<OTPRequest> requests)
      throws MailCouldNotBeSendException, WrongArgumentException, SpecifiedSubGroupDoesntExistsException {
        requests.forEach(request -> {
            var code = generateNewCode();
            var mail = createMail(request, code);
            
            if (request.getGeneralGroupName().length() > 3) {
                throw new WrongArgumentException(
                  "Wrong general group provided. Make sure you are not providing subgroup. (f.e 12K1 -> wrong, 12K -> good)");
            }
            
            if (!generalGroupExists(request.getGeneralGroupName())) {
                throw new SpecifiedGeneralGroupDoesntExistsException();
            }
            
            try {
                emailService.send(mail);
            } catch (MessagingException e) {
                throw new MailCouldNotBeSendException("Couldn't send mail for group: " + request.getGeneralGroupName());
            }
            
            
            var generalGroup = generalGroupRepository.findByName(request.getGeneralGroupName());
            
            if (generalGroup.isEmpty()) {
                generalGroup = Optional.of(generalGroupRepository.save(new GeneralGroup(null, request.getGeneralGroupName())));
            }
            
            var user = User
              .builder()
              .email(request.getEmail())
              .generalGroup(generalGroup.get())
              .role(Role.REPRESENTATIVE)
              .isActive(true)
              .build();
            
            userRepository.save(user);
            
            repository.save(new OTPCode(code, generalGroup.get()));
        });
    }
    
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
    
    private boolean generalGroupExists (String name) {
        Set<String> list = timetableService.getGeneralGroupList().stream().map(item -> {
            var lastIndex = item.length() - 1;
            if (Character.isDigit(item.charAt(lastIndex))) {
                return item.substring(0, lastIndex);
            }
            return item;
        }).collect(Collectors.toSet());
        
        return list.contains(name);
    }
    
}
