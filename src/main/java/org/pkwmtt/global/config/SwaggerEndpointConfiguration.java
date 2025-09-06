package org.pkwmtt.global.config;


import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class SwaggerEndpointConfiguration {
    
    private final Environment environment;
    
    //Add text field for api key to every request that need authentication with it
    @Bean
    public GroupedOpenApi publicEndpointCustomizer () {
        String apiPrefix = environment.getProperty("apiPrefix", "");
        
        return GroupedOpenApi.builder().group("all") // single group
                             .pathsToMatch(apiPrefix + "/**", "/admin/**").addOpenApiCustomizer(openApi -> {
              Paths paths = openApi.getPaths();
              
              paths.forEach((path, pathItem) -> pathItem.readOperations().forEach(operation -> {
                  if (path.startsWith("/admin")) {
                      addHeaderIfMissing(
                        operation,
                        "X-ADMIN-KEY",
                        "Admin API key",
                        "Admin-only endpoint",
                        "Requires X-ADMIN-KEY header",
                        "admin"
                      );
                  } else if (path.startsWith(apiPrefix)) {
                      addHeaderIfMissing(
                        operation,
                        "X-API-KEY",
                        "Your API key",
                        "Public API endpoint",
                        "Requires X-API-KEY header",
                        "public"
                      );
                  }
              }));
          }).build();
    }
    
    private void addHeaderIfMissing (Operation operation, String headerName, String headerDescription, String summary, String description, String tag) {
        operation.setSummary(summary);
        operation.setDescription(description);
        operation.addTagsItem(tag);
        operation.addParametersItem(new Parameter()
                                      .name(headerName)
                                      .in("header")
                                      .required(true)
                                      .description(headerDescription)
                                      .schema(new StringSchema()));
    }
    
    
}
