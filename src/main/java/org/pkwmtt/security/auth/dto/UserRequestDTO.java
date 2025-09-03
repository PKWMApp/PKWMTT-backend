package org.pkwmtt.security.auth.dto;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String otp_code;
    private String email;
}
