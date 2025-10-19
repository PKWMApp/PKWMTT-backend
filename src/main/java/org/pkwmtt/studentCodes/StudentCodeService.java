package org.pkwmtt.studentCodes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.exceptions.WrongArgumentException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.SuperiorGroup;
import org.pkwmtt.examCalendar.entity.StudentCode;
import org.pkwmtt.examCalendar.entity.Representative;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.examCalendar.repository.SuperiorGroupRepository;
import org.pkwmtt.examCalendar.repository.RepresentativeRepository;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.mail.EmailService;
import org.pkwmtt.mail.dto.MailDTO;
import org.pkwmtt.security.jwt.JwtService;
import org.pkwmtt.studentCodes.dto.StudentCodeRequest;
import org.pkwmtt.studentCodes.repository.StudentCodeRepository;
import org.pkwmtt.security.authentication.JwtAuthenticationService;
import org.pkwmtt.security.authentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.jwt.dto.RepresentativeDTO;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentCodeService {
    private final StudentCodeRepository studentCodeRepository;
    private final RepresentativeRepository representativeRepository;
    private final SuperiorGroupRepository superiorGroupRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final TimetableService timetableService;
    
    //TODO increase usage counter
    
    public JwtAuthenticationDto generateTokenForUser (String code)
      throws StudentCodeNotFoundException, WrongStudentCodeFormatException, UserNotFoundException {
        var superiorGroup = this.getSuperiorGroupAssignedToCode(code);
        var representative = representativeRepository
          .findBySuperiorGroup(superiorGroup)
          .orElseThrow(() -> new UserNotFoundException("No representative is assigned to this code."));
        
        var userEmail = representative.getEmail();
        
        String token = jwtService.generateAccessToken(
          new RepresentativeDTO()
            .setEmail(userEmail)
            .setRole(Role.REPRESENTATIVE)
            .setGroup(superiorGroup.getName())
        );
        
        var refreshToken = jwtAuthenticationService.getNewUserRefreshToken(representative);
        
        return JwtAuthenticationDto
          .builder()
          .accessToken(token)
          .refreshToken(refreshToken)
          .build();
    }
    
    public List<SendStudentCodeFailure> sendStudentCodes (List<StudentCodeRequest> requests) {
        // Collect per-group failures and return them to the caller so they can decide what to do.
        var failures = new java.util.ArrayList<SendStudentCodeFailure>();
        for (StudentCodeRequest request : requests) {
            try {
                sendStudentCode(request);
            } catch (Exception e) {
                String group = request.getSuperiorGroupName();
                String reason = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                reason = reason.replaceAll("\\r?\\n", " ");
                failures.add(new SendStudentCodeFailure(group, reason, e.getClass().getSimpleName()));
            }
        }
        
        return failures;
    }
    
    public void sendStudentCode (StudentCodeRequest request)
      throws MailCouldNotBeSendException, WrongArgumentException, SpecifiedSubGroupDoesntExistsException, IllegalArgumentException, JsonProcessingException {
        var code = generateNewCode();
        var mail = createMail(request, code);
        var groupName = request.getSuperiorGroupName();
        
        validateGroupNameFormat(groupName);
        
        if (!generalGroupExists(groupName)) { // Check if general group with provided name exists
            throw new SpecifiedGeneralGroupDoesntExistsException();
        }
        
        var superiorGroup = findOrCreateSuperiorGroup(groupName);
        ensureNoExistingRepresentativeByEmail(request.getEmail());
        
        var representative = buildRepresentative(request.getEmail(), superiorGroup.get());
        replaceExistingRepresentativeForGroup(superiorGroup.get());
        representativeRepository.save(representative);
        studentCodeRepository.save(new StudentCode(code, superiorGroup.get()));
        
        sendEmailOrThrow(mail, groupName);
    }
    
    private void validateGroupNameFormat (String groupName) throws WrongArgumentException {
        var groupNameLength = groupName.length();
        if (groupNameLength > 3 && Character.isDigit(
          groupName.charAt(groupNameLength - 1))) { //Check general group name
            throw new WrongArgumentException(
              "Wrong general group provided. Make sure you are not providing subgroup. (f.e 12K1 -> wrong, 12K -> good)");
        }
    }
    
    private Optional<SuperiorGroup> findOrCreateSuperiorGroup (String groupName) {
        var superiorGroup = superiorGroupRepository.findByName(groupName);
        if (superiorGroup.isPresent()) {
            if (studentCodeRepository.existsBySuperiorGroup(superiorGroup.get())) {
                studentCodeRepository.deleteBySuperiorGroup(superiorGroup.get());
            }
            return superiorGroup;
        } else {
            return Optional.of(superiorGroupRepository.save(new SuperiorGroup(null, groupName)));
        }
    }
    
    private void ensureNoExistingRepresentativeByEmail (String email) {
        var representativeByEmail = representativeRepository.findByEmail(email);
        if (representativeByEmail.isPresent()) {
            throw new UserAlreadyAssignedException(
              "Representative with email: " + email + " already has assigned different group.");
        }
    }
    
    private void sendEmailOrThrow (MailDTO mail, String groupName) throws MailCouldNotBeSendException {
        try {
            emailService.send(mail);
        } catch (MessagingException e) {
            throw new MailCouldNotBeSendException("Couldn't send mail for group: " + groupName);
        }
    }
    
    private Representative buildRepresentative (String email, SuperiorGroup superiorGroup) {
        return Representative
          .builder()
          .email(email)
          .superiorGroup(superiorGroup)
          .isActive(true)
          .build();
    }
    
    private void replaceExistingRepresentativeForGroup (SuperiorGroup superiorGroup) {
        representativeRepository
          .findBySuperiorGroup(superiorGroup)
          .ifPresent(value -> representativeRepository.deleteRepresentativeByEmail(value.getEmail()));
    }
    
    private SuperiorGroup getSuperiorGroupAssignedToCode (String code)
      throws StudentCodeNotFoundException, WrongStudentCodeFormatException {
        this.validateCode(code);
        Optional<StudentCode> result = studentCodeRepository.findByCode(code);
        if (result.isEmpty()) {
            throw new StudentCodeNotFoundException();
        }
        return result.get().getSuperiorGroup();
    }
    
    private void validateCode (String code) throws WrongStudentCodeFormatException {
        if (code.length() != 6) {
            throw new WrongStudentCodeFormatException("Code should be 6 characters long.");
        }
        
        String regex = "^[A-Z0-9]{6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);
        
        if (!matcher.find()) {
            throw new WrongStudentCodeFormatException("Wrong format of provided code.");
        }
    }
    
    
    private MailDTO createMail (StudentCodeRequest request, String code) {
        return new MailDTO()
          .setTitle("Kod Starosty " + request.getSuperiorGroupName())
          .setRecipient(request.getEmail())
          .setDescription(request.getMailMessage(code));
    }
    
    private String generateNewCode () {
        String AVAILABLE_CHARS = "ABCDEFGHIJKLMNOPQRSTUWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        
        do {
            code.setLength(0);
            for (int i = 0; i < 6; i++) {
                code.append(AVAILABLE_CHARS.charAt(random.nextInt(AVAILABLE_CHARS.length())));
            }
        } while (studentCodeRepository.findByCode(code.toString()).isPresent());
        
        return code.toString();
    }
    
    private boolean generalGroupExists (String name) throws JsonProcessingException {
        Set<String> list = timetableService
          .getGeneralGroupList()
          .stream()
          .map(item -> {
              var lastIndex = item.length() - 1;
              if (Character.isDigit(item.charAt(lastIndex))) {
                  return item.substring(0, lastIndex);
              }
              return item;
          }).collect(Collectors.toSet());
        
        return list.contains(name);
    }
    
}
