package org.pkwmtt.otp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OTPRequest {
    private String email;
    private String generalGroupName;
    
    public String getMailMessage (String code) {
        return String.format(
          """
            <b>Kod starosty %s</b><br/>
            Poniżej znajduje się kod służący do ulepszenia wersji aplikacji do poziomu starosty. <br/>
            Dzięki temu będziesz mógł dodawać oraz usuwać egzaminy dla swojego kierunku w kalendarzu aplikacji.<br/>
            Wpisz kod w <i>[Ustawienia > Wpisz kod]</i>, albo przekaż go osobie odpowiedzialnej za kalendarz egzaminów.<br/>
            Twój kod: <b>%s</b> <br/>
            Na wykorzystanie kodu masz 1 dzień. <br/>
            """, generalGroupName, code
        );
    }
}
