package org.pkwmtt.security.token.dto;

import lombok.Data;
import org.pkwmtt.entity.User;
import org.pkwmtt.enums.Role;

@Data
public class UserDTO {
    private String email;
    private String group;
    private Role role;

    public UserDTO(User user){
        this.email = user.getEmail();
        this.group = user.getGeneralGroup() != null ? user.getGeneralGroup().getName() : null;
        this.role = user.getRole();
    }
}
