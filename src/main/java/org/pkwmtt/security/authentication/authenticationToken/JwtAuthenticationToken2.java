package org.pkwmtt.security.authentication.authenticationToken;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

//TODO: delete
public class JwtAuthenticationToken2 extends UsernamePasswordAuthenticationToken {

    @Getter
    private String examGroup;

    public JwtAuthenticationToken2(Object principal, Collection<? extends GrantedAuthority> authorities, String group) {
        super(principal, null, authorities);
        this.examGroup = group;
    }

}
