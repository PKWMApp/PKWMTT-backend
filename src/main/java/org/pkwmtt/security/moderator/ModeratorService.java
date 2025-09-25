package org.pkwmtt.security.moderator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.GeneralGroup;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.examCalendar.repository.GeneralGroupRepository;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.security.token.JwtService;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final TimetableService timetableService;
    private final GeneralGroupRepository generalGroupRepository;

    public String generateTokenForModerator(String password) {
        return moderatorRepository.findAll()
                .stream()
                .filter(m -> passwordEncoder.matches(m.getPassword(), password))
                .findFirst()
                .map(m -> jwtService.generateToken(m.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void addUser(UserDto userDto) {
        User user = User.builder()
                .email(userDto.getEmail())
                .generalGroup(checkGeneralGroup(userDto.getGeneralGroup()))
                .isActive(false)
                .role(Role.REPRESENTATIVE)
                .build();
        userRepository.save(user);
    }

    private GeneralGroup checkGeneralGroup(String name) {
        Set<String> generalGroups = timetableService.getGeneralGroupList().stream().map(item -> {
            var lastIndex = item.length() - 1;
            if (Character.isDigit(item.charAt(lastIndex))) {
                return item.substring(0, lastIndex);
            }
            return item;
        }).collect(Collectors.toSet());

        if (!generalGroups.contains(name))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No group exists");        //TODO: change exception type

        return generalGroupRepository.findByName(name)
                .orElseGet(() -> generalGroupRepository.save(
                        GeneralGroup.builder()
                                .name(name)
                                .build()
                ));
    }
}
