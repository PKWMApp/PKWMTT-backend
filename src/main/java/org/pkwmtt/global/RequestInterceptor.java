package org.pkwmtt.global;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.exceptions.IncorrectApiKeyValue;
import org.pkwmtt.exceptions.MissingHeaderException;
import org.pkwmtt.security.apiKey.ApiKeyService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test & !database") //Skip on tests
public class RequestInterceptor implements HandlerInterceptor {
    
    private final ApiKeyService apiKeyService;
    
    @Override
    public boolean preHandle (@NonNull HttpServletRequest request,
                              @NonNull HttpServletResponse response,
                              @NonNull Object handler) throws MissingHeaderException {
        String apiKey = request.getHeader("X-API-KEY");
        
        if (apiKey == null || apiKey.isBlank()) {
            throw new MissingHeaderException("X-API-KEY");
        }
        
        try {
            apiKeyService.validateApiKey(apiKey, Role.REPRESENTATIVE);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalException("Internal server error with validating API key.");
        }
        
        return true;
    }
}
