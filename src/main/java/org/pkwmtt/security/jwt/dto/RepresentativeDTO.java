package org.pkwmtt.security.jwt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.pkwmtt.examCalendar.entity.SuperiorGroup;
import org.pkwmtt.examCalendar.entity.Representative;
import org.pkwmtt.examCalendar.enums.Role;

import java.util.Optional;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class RepresentativeDTO {
    private String email;
    private String group;
    private Role role;
    
    public RepresentativeDTO (Representative user) {
        this.email = user.getEmail();
        this.role = Role.REPRESENTATIVE;
        this.group = Optional.ofNullable(user.getSuperiorGroup()).map(SuperiorGroup::getName).orElse(null);
    }
}
