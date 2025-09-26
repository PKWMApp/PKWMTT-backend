package org.pkwmtt.global.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerEndpointConfiguration {
    
    private final Environment environment;
    
    @Value("${swagger.url:http://localhost:8080}")
    String url;
    
    @Bean
    public OpenAPI setOpenApiProtocol () {
        return new OpenAPI().servers(List.of(new Server().url(url)));
    }
    
    
    //Add text field for api key to every request that need authentication with it
    @Bean
    public GroupedOpenApi publicEndpointCustomizer () {
        String apiPrefix = environment.getProperty("apiPrefix", "");
        
        return GroupedOpenApi.builder().group("all") // single group
                             .pathsToMatch("/**").addOpenApiCustomizer(openApi -> {
              Paths paths = openApi.getPaths();
              
              paths.forEach((path, pathItem) -> pathItem.readOperationsMap().forEach(((httpMethod, operation) -> {
                  if (path.startsWith("/admin")) {
                      addHeaderIfMissing(
                        operation,
                        "X-ADMIN-KEY",
                        "Admin API key",
                        "Admin-only endpoint",
                        "Requires X-ADMIN-KEY header",
                        "admin",
                        true
                      );
                  } else if (path.startsWith(apiPrefix)) {
                      addHeaderIfMissing(
                        operation,
                        "X-API-KEY",
                        "Your API key",
                        "Public API endpoint",
                        "Requires X-API-KEY header",
                        "public",
                        true
                      );
                  }
              })));
          }).build();
    }
    
    private void addHeaderIfMissing (Operation operation, String headerName, String headerDescription, String summary, String description, String tag, boolean required) {
        operation.setSummary(summary);
        operation.setDescription(description);
        operation.addTagsItem(tag);
        operation.addParametersItem(new Parameter()
                                      .name(headerName)
                                      .in("header")
                                      .required(required)
                                      .description(headerDescription)
                                      .schema(new StringSchema()));
    }
    
    
}
