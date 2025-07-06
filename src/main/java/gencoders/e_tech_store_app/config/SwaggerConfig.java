package gencoders.e_tech_store_app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin-apis")
                .pathsToMatch("/api/admin/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .addSecurityItem(new SecurityRequirement().addList("bearerAuth")))
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Tech Store API")
                        .version("1.0")
                        .description("API documentation for E-Tech Store"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
