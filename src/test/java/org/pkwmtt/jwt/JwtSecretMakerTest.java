package org.pkwmtt.jwt;

import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtSecretMakerTest {

    @Test
    public void generateSecretKey(){
        SecretKey key = Jwts.SIG.HS512.key().build();
        String encodedKey = DatatypeConverter.printHexBinary(key.getEncoded());
        System.out.printf("\nKey = [%s]\n", encodedKey);
        String base64Secret = Base64.getEncoder().encodeToString(encodedKey.getBytes(StandardCharsets.UTF_8));
        System.out.println(base64Secret);
    }
}
