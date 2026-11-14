package fr.mm.formation.location.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentation() {
        return new OpenAPI().info(new Info()
                .title("API de location de matériel")
                .description("Documentation produite par SpringDoc à partir des contrôleurs.")
                .version("1.0.0"));
    }
}