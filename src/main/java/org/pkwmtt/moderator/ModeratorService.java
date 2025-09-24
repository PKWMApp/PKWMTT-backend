package org.pkwmtt.moderator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.security.token.JwtService;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;
    private final JwtService jwtService;

    public String generateTokenForModerator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
