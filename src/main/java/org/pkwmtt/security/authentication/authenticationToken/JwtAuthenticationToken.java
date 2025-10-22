package org.pkwmtt.security.authentication.authenticationToken;

import org.pkwmtt.examCalendar.mapper.GroupMapper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String principal;
    private String jwtToken;
    private String group;


    /**
     * This constructor can be safely used by any code that wishes to create a JwtAuthenticationToken,
     * as the isAuthenticated() will return false
     * @param jwtToken
     */
    public JwtAuthenticationToken(String jwtToken) {
        super(null);
        this.jwtToken = jwtToken;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by AuthenticationManager or AuthenticationProvider
     * implementations that are satisfied with producing a trusted (i.e. isAuthenticated() = true)
     * authentication token. It refers to users with authorities for specific group only
     * @param principal
     * @param authorities
     * @param group
     */
    public JwtAuthenticationToken(String principal, Collection<? extends GrantedAuthority> authorities, String group) {
        super(authorities);
        this.principal = principal;
        this.jwtToken = null;
        this.group = group;
        super.setAuthenticated(true);
    }

    /**
     * This constructor should only be used by AuthenticationManager or AuthenticationProvider
     * implementations that are satisfied with producing a trusted (i.e. isAuthenticated() = true)
     * authentication token. It refers to users without authorities for specific groups
     * @param principal
     * @param authorities
     */
    public JwtAuthenticationToken(String principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.jwtToken = null;
        this.group = null;
        super.setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return this.jwtToken;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.jwtToken = null;
    }

//    TODO: adjust for authorization
    public boolean compareGroups(String generalGroup) {
        String provided = GroupMapper.trimLastDigit(generalGroup);
        return this.group.equals(provided);
    }
}