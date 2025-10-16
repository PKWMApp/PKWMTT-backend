package org.pkwmtt.otp;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.mysql.cj.exceptions.WrongArgumentException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.pkwmtt.exceptions.OTPCodeNotFoundException;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WrongOTPFormatException;
import org.pkwmtt.otp.dto.OTPRequest;
import org.pkwmtt.otp.repository.OTPCodeRepository;
import org.pkwmtt.security.auhentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.token.repository.UserRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("database")
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class OTPServiceTest {
    
    @Autowired
    private OTPService otpService;
    
    @Autowired
    private OTPCodeRepository otpCodeRepository;

    @Autowired
    private UserRefreshTokenRepository userRefreshTokenRepository;
    
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@localhost", "test"))
      .withPerMethodLifecycle(true);
    
    @Test
    void shouldSendCorrectMailWithRepresentativePayload () {
        //given
        List<OTPRequest> requests = List.of(new OTPRequest("test2@localhost", "12K"));
        Pattern pattern = Pattern.compile("[A-Z0-9]{6}");
        //when
        otpService.sendOTPCodesForManyGroups(requests);
        
        //then
        assertAll(() -> {
            assertTrue(greenMail.waitForIncomingEmail(1));
            
            MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
            assertEquals("Kod Starosty 12K", receivedMessage.getSubject());
            assertEquals("test2@localhost", receivedMessage.getAllRecipients()[0].toString());
            
            Matcher matcher = pattern.matcher(Objects.requireNonNull(extractBody(receivedMessage)));
            assertTrue(matcher.find());
            System.out.println(matcher.group(0));
            assertTrue(otpCodeRepository.existsOTPCodeByCode(matcher.group(0)));
        });
    }
    
    @Test
    void shouldThrow_WrongArgumentException () {
        //given
        List<OTPRequest> requests = List.of(new OTPRequest("test@localhost", "12K1"));
        //when
        //then
        assertThrows(WrongArgumentException.class, () -> otpService.sendOTPCodesForManyGroups(requests));
    }
    
    @Test
    void shouldThrow_SpecifiedGeneralGroupDoesntExistsException () {
        //given
        List<OTPRequest> requests = List.of(new OTPRequest("test@localhost", "XXXX"));
        //when
        //then
        assertThrows(SpecifiedGeneralGroupDoesntExistsException.class, () -> otpService.sendOTPCodesForManyGroups(requests));
    }

    @Test
    void shouldGenerateTokenForRepresentative () throws Exception {
        //given
        List<OTPRequest> requests = List.of(new OTPRequest("test@localhost", "12K"));
        Pattern otpPattern = Pattern.compile("[A-Z0-9]{6}");
        Pattern tokenPattern = Pattern.compile("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+");
        
        //when
        otpService.sendOTPCodesForManyGroups(requests); //generate mail with code
        greenMail.waitForIncomingEmail(1); // fetch mail
        
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        Matcher otpMatcher = otpPattern.matcher(Objects.requireNonNull(extractBody(receivedMessage))); //get content
        
        final String code;
        if (otpMatcher.find()) {
            code = otpMatcher.group();
        } else {
            code = "";
            fail("Code not found");
        }
        
        JwtAuthenticationDto token = otpService.generateTokenForRepresentative(code); //generate token

        //then
        assertAll(() -> {
            assertNotNull(token);

            Matcher tokenMatcher = tokenPattern.matcher(token.getAccessToken());
            assertNotNull(token.getRefreshToken());
            assertTrue(tokenMatcher.find());
            assertFalse(otpCodeRepository.existsOTPCodeByCode(code));
            assertFalse(userRefreshTokenRepository.findAll().isEmpty());
        });
    }
    
    @Test
    void shouldThrow_WrongOTPFormatException_wrongCharacters () {
        assertThrows(WrongOTPFormatException.class, () -> otpService.generateTokenForRepresentative("XXXXX#"));
    }
    
    @Test
    void shouldThrow_WrongOTPFormatException_tooLongCode () {
        assertThrows(WrongOTPFormatException.class, () -> otpService.generateTokenForRepresentative("X".repeat(7)));
    }
    
    @Test
    void shouldThrow_OTPCodeNotFoundException () {
        assertThrows(OTPCodeNotFoundException.class, () -> otpService.generateTokenForRepresentative("X".repeat(6)));
    }
    
    private String extractBody (Part part) throws Exception {
        if (part.isMimeType("text/plain") || part.isMimeType("text/html")) {
            return (String) part.getContent();
        }
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String result = extractBody(mp.getBodyPart(i));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
}