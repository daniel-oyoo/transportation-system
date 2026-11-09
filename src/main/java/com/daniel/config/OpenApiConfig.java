package com.daniel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transportationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Easy Coach Kenya Demo Booking API")
                        .description("Sample trip search and booking API. Not an official Easy Coach service.")
                        .version("v1")
                        .contact(new Contact().name("Demo project maintainer"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}