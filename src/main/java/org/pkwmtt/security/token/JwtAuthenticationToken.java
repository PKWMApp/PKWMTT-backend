package org.pkwmtt.security.token;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    @Getter
    private String examGroup;

    public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities,  String group) {
        super(principal, null, authorities);
        this.examGroup = group;
    }

}
