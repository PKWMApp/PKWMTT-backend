package org.pkwmtt.global.config;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.global.RequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    //During tests RequestInterceptor isn't required
    private final Optional<RequestInterceptor> requestInterceptor;
    private final Environment environment;
    
    @Override
    public void addInterceptors (InterceptorRegistry registry) {
        String apiPrefix = environment.getProperty("apiPrefix", "");
        requestInterceptor.ifPresent(interceptor -> registry.addInterceptor(interceptor).addPathPatterns(apiPrefix + "/**"));
    }
}