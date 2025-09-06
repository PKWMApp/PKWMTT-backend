package org.pkwmtt.security.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.InternalException;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.exceptions.IncorrectApiKeyValue;
import org.pkwmtt.exceptions.MissingHeaderException;
import org.pkwmtt.security.apiKey.ApiKeyService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AdminRequestInterceptor implements HandlerInterceptor {
    private final ApiKeyService apiKeyService;
    
    @Override
    public boolean preHandle (@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String headerName = "X-ADMIN-KEY";
        try {
            String providedApiKey = request.getHeader(headerName);
            
            if (providedApiKey == null || providedApiKey.isBlank()) {
                throw new MissingHeaderException(headerName);
            }
            
            apiKeyService.validateApiKey(providedApiKey, Role.ADMIN);
        } catch (IncorrectApiKeyValue | MissingHeaderException e) {
            throw new IncorrectApiKeyValue();
        } catch (Exception e) {
            throw new InternalException("Internal server error with validating API key.");
        }
        
        return true;
    }
    
}
