package org.pkwmtt.global;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
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
    public boolean preHandle (HttpServletRequest request, HttpServletResponse response, Object handler) {
        
        
        String headerName = "x-api-key";
        try {
            String providedApiKey = request.getHeader(headerName);
            
            if (providedApiKey == null || providedApiKey.isBlank()) {
                throw new MissingHeaderException(headerName);
            }
            
            apiKeyService.validateApiKey(providedApiKey);
        } catch (IncorrectApiKeyValue | MissingHeaderException e) {
            throw new IncorrectApiKeyValue();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalException("Internal server error with validating API key.");
        }
        
        return true;
    }
}
