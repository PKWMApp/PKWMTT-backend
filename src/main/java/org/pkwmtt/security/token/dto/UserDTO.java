package org.pkwmtt.security.token.dto;

import lombok.Data;
import org.pkwmtt.examCalendar.entity.GeneralGroup;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.examCalendar.enums.Role;

import java.util.Optional;

@Data
public class UserDTO {
    private String email;
    private String group;
    private Role role;

    public UserDTO(User user){
        this.email = user.getEmail();
        this.role = user.getRole();
        this.group = Optional.ofNullable(user.getGeneralGroup())
                .map(GeneralGroup::getName)
                .orElse(null);
    }
}
