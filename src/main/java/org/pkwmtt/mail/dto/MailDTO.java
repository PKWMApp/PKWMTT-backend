package org.pkwmtt.mail.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class MailDTO {
    private String recipient;
    private String title;
    private String description;
}
