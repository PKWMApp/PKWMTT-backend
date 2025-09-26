package org.pkwmtt.security.token.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.security.token.JwtService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtFilterTest {

    private JwtService jwtService;
    private UserRepository userRepository;
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userRepository = mock(UserRepository.class);
        jwtFilter = new JwtFilter();
        jwtFilter.jwtService = jwtService;
        jwtFilter.userRepository = userRepository;

        SecurityContextHolder.clearContext();
    }

    @Test
    void givenValidToken_whenDoFilter_thenAuthenticationSet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        User mockUser = mock(User.class);
        when(mockUser.getRole()).thenReturn(Role.valueOf("ADMIN"));
        when(mockUser.getEmail()).thenReturn("user@example.com");

        when(jwtService.getSubject("validToken")).thenReturn("user@example.com");
        when(jwtService.validateToken(eq("validToken"), any(User.class))).thenReturn(true);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtService.extractClaim(any(String.class), any(Function.class))).thenReturn("ADMIN");
        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
