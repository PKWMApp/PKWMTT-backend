package org.pkwmtt.studentCodes;

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
import org.pkwmtt.security.token.JwtService;
import org.pkwmtt.studentCodes.dto.StudentCodeRequest;
import org.pkwmtt.studentCodes.repository.StudentCodeRepository;
import org.pkwmtt.security.auhentication.JwtAuthenticationService;
import org.pkwmtt.security.auhentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.token.dto.RepresentativeDTO;
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

    public JwtAuthenticationDto generateTokenForRepresentative (String code)
      throws StudentCodeNotFoundException, WrongOTPFormatException, UserNotFoundException {
        var superiorGroup = this.getSuperiorGroupAssignedToCode(code);
        var representative = representativeRepository
          .findBySuperiorGroup(superiorGroup)
          .orElseThrow(() -> new UserNotFoundException("No representative is assigned to this code."));

        var userEmail = representative.getEmail();
        String token = jwtService.generateAccessToken(new RepresentativeDTO()
                                                  .setEmail(userEmail)
                                                  .setRole(Role.REPRESENTATIVE)
                                                  .setGroup(superiorGroup.getName()));
        studentCodeRepository.deleteByCode(code);
        return JwtAuthenticationDto.builder()
                .accessToken(token)
                .refreshToken(jwtAuthenticationService.getNewUserRefreshToken(representative))
                .build();
    }

    public void sendOTPCodesForManyGroups (List<StudentCodeRequest> requests)
      throws MailCouldNotBeSendException, WrongArgumentException, SpecifiedSubGroupDoesntExistsException, IllegalArgumentException {
        requests.forEach(this::sendOtpCode);
    }

    public void sendOtpCode (StudentCodeRequest request)
      throws MailCouldNotBeSendException, WrongArgumentException, SpecifiedSubGroupDoesntExistsException, IllegalArgumentException {
        var code = generateNewCode();
        var mail = createMail(request, code);
        var groupName = request.getSuperiorGroup();
        var groupNameLength = groupName.length();

        if (groupNameLength > 3 && Character.isDigit(
          groupName.charAt(groupNameLength - 1))) { //Check general group name
            throw new WrongArgumentException(
              "Wrong general group provided. Make sure you are not providing subgroup. (f.e 12K1 -> wrong, 12K -> good)");
        }

        if (!generalGroupExists(groupName)) { // Check if general group with provided name exists
            throw new SpecifiedGeneralGroupDoesntExistsException();
        }

        var superiorGroup = superiorGroupRepository.findByName(groupName);
        if (superiorGroup.isPresent()) {
            if (studentCodeRepository.existsBySuperiorGroup(
              superiorGroup.get())) {
                studentCodeRepository.deleteBySuperiorGroup(superiorGroup.get());
            }
        } else {
            superiorGroup = Optional.of(superiorGroupRepository.save(new SuperiorGroup(null, groupName)));
        }
        var representativeByEmail = representativeRepository.findByEmail(request.getEmail());
        if (representativeByEmail.isPresent()) {
                throw new UserAlreadyAssignedException(
                  "Representative with this email is already assigned group.");
        }
        try {
            emailService.send(mail);
        } catch (MessagingException e) {
            throw new MailCouldNotBeSendException("Couldn't send mail for group: " + groupName);
        }
        var representative = Representative
          .builder()
          .email(request.getEmail())
          .superiorGroup(superiorGroup.get())
          .isActive(true)
          .build();
        representativeRepository
          .findBySuperiorGroup(superiorGroup.get())
          .ifPresent(value -> representativeRepository.deleteRepresentativeByEmail(value.getEmail()));
        representativeRepository.save(representative);
        studentCodeRepository.save(new StudentCode(code, superiorGroup.get()));
    }

    private SuperiorGroup getSuperiorGroupAssignedToCode (String code)
      throws StudentCodeNotFoundException, WrongOTPFormatException {
        this.validateCode(code);
        Optional<StudentCode> result = studentCodeRepository.findByCode(code);
        if (result.isEmpty()) {
            throw new StudentCodeNotFoundException();
        }
        return result.get().getSuperiorGroup();
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


    private MailDTO createMail (StudentCodeRequest request, String code) {
        return new MailDTO()
          .setTitle("Kod Starosty " + request.getSuperiorGroup())
          .setRecipient(request.getEmail())
          .setDescription(request.getMailMessage(code));
    }

    private String generateNewCode () {
        String availableCharacters = "ABCDEFGHIJKLMNOPQRSTUWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();

        do {
            code.setLength(0);
            for (int i = 0; i < 6; i++) {
                code.append(availableCharacters.charAt(random.nextInt(availableCharacters.length())));
            }
        } while (studentCodeRepository.findByCode(code.toString()).isPresent());

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
