package org.pkwmtt.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.pkwmtt.examCalendar.entity.Representative;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.examCalendar.repository.RepresentativeRepository;
import org.pkwmtt.moderator.ModeratorRepository;
import org.pkwmtt.security.authentication.authenticationToken.JwtAuthenticationToken;
import org.pkwmtt.security.authentication.authenticationToken.JwtAuthenticationToken2;
import org.pkwmtt.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;

    private final RepresentativeRepository representativeRepository;

    private final ModeratorRepository moderatorRepository;
    
    /**
     * Filters incoming HTTP requests to validate JWT tokens.
     *
     * <p>This filter:
     * - Extracts the JWT token from the Authorization header.
     * - Validates the token using JwtService.
     * - Loads the user from UserRepository.
     * - Sets the Spring Security Authentication in the SecurityContext.
     *
     * @param request     the HttpServletRequest
     * @param response    the HttpServletResponse
     * @param filterChain the FilterChain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal (HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String subject = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            subject = jwtService.getSubject(token);
        }
        
        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

//            Authorization auth = new JwtAuthenticationToken();
            
            
            if (role.equals("MODERATOR")) {
                filterModerator(request, token, subject);
            } else {
                filterUser(request, token, subject);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private void filterModerator (HttpServletRequest request, String token, String subject) {
        UUID uuid = UUID.fromString(subject);
        moderatorRepository.findById(uuid).orElseThrow(); // TODO: add exception type
        
        if (jwtService.validateAccessToken(token, subject)) {
            List<SimpleGrantedAuthority> authorities = List.of(
              new SimpleGrantedAuthority("ROLE_" + "MODERATOR")
            );
            
            UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                subject,
                null,
                authorities
              );
            
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
        }
    }
    
    private void filterUser (HttpServletRequest request, String token, String subject) {
        //        TODO: handle invalid email
        Representative representative = representativeRepository.findByEmail(subject).orElseThrow();
        
        if (jwtService.validateAccessToken(token, representative)) {
            List<SimpleGrantedAuthority> authorities = List.of(
              new SimpleGrantedAuthority("ROLE_" + Role.REPRESENTATIVE)
            );
            
            UsernamePasswordAuthenticationToken authToken =
              new JwtAuthenticationToken2(
                representative.getEmail(),
                authorities,
                jwtService.extractClaim(token, claims -> claims.get("group", String.class))
              );
            
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}
