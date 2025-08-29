package org.pkwmtt.security.token.dto;

import lombok.Data;
import org.pkwmtt.entity.GeneralGroup;
import org.pkwmtt.entity.User;
import org.pkwmtt.enums.Role;

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
