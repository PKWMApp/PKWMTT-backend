package org.pkwmtt.security.token.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.security.moderator.ModeratorRepository;
import org.pkwmtt.security.token.JwtAuthenticationToken;
import org.pkwmtt.security.token.JwtService;
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
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModeratorRepository moderatorRepository;

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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String subject = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            subject = jwtService.getSubject(token);
        }

        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));


            if (role.equals("MODERATOR"))
                filterModerator(request, token, subject);
            else
                filterUser(request, token, subject);
        }

        filterChain.doFilter(request, response);
    }

    private void filterModerator(HttpServletRequest request, String token, String subject) {
        UUID uuid = UUID.fromString(subject);
        moderatorRepository.findById(uuid).orElseThrow();       //TODO: add exception type

        if (jwtService.validateToken(token, subject)) {
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

    private void filterUser(HttpServletRequest request, String token, String subject) {
//        TODO: handle invalid email
        User user = userRepository.findByEmail(subject).orElseThrow();

        if (jwtService.validateToken(token, user)) {
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole())
            );

            UsernamePasswordAuthenticationToken authToken =
                    new JwtAuthenticationToken(
                            user.getEmail(),
                            authorities,
                            jwtService.extractClaim(token, claims -> claims.get("group", String.class))
                    );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}
