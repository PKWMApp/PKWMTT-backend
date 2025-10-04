package org.pkwmtt.security.auth;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.repository.ExamRepository;
import org.pkwmtt.exceptions.NoSuchElementWithProvidedIdException;
import org.pkwmtt.security.token.JwtAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static org.pkwmtt.examCalendar.mapper.GroupMapper.extractSuperiorGroup;

//TODO: handle AccessDeniedException

@Service
@RequiredArgsConstructor
public class PreAuthorizationService {

    private final ExamRepository examRepository;

    /**
     * verifies if user has authorities to add new resource
     * @param newGroups set of provided groups
     */
    public boolean verifyGroupPermissionsForNewResource(Set<String> newGroups){
        String userGroup = getUserGroup();
        return extractSuperiorGroup(newGroups).equals(userGroup);
    }

    /**
     * verifies if user has authorities to modify existing resource
     * @param examId id of existing resource
     */
    public boolean verifyGroupPermissionsForExistingResource(Integer examId){
        String userGroup = getUserGroup();
        Set<String> generalGroupsOfExam = examRepository.findGroupsByExamId(examId)
                .stream()
                .filter(group -> group.matches("^\\d.*"))
                .collect(Collectors.toSet());
        return extractSuperiorGroup(generalGroupsOfExam).equals(userGroup);
    }

    /**
     * verifies if user had authorities to replace existing resource with new one
     * @param newGroups set of groups of new resource
     * @param examId id of existing resource
     */
    public boolean verifyGroupPermissionsForModifiedResource(Set<String> newGroups, Integer examId){
        examRepository.findById(examId).orElseThrow(() -> new NoSuchElementWithProvidedIdException(examId));
        return verifyGroupPermissionsForNewResource(newGroups) && verifyGroupPermissionsForExistingResource(examId);
    }

    /**
     * @return superior group identifier (e.g. 12K) of currently authenticated user
     * @throws AccessDeniedException when user doesn't have assigned group
     */
    private String getUserGroup() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication))
            throw new AccessDeniedException("You don't have permission to access this group");

        String group = jwtAuthentication.getExamGroup();
        if(group == null || group.isBlank())
            throw  new AccessDeniedException("You don't have access to any group");

        return group;
    }
}