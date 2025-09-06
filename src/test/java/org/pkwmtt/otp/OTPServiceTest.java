package org.pkwmtt.otp;

import com.mysql.cj.exceptions.WrongArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.otp.dto.OTPRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("database")
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class OTPServiceTest {
    
    @Autowired
    private OTPService otpService;
    
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
    
    
}