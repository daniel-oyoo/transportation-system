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
                        .title("Transportation & Seat Reservation System API")
                        .description("REST API documentation for managing cars, seats, and customer travel bookings.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Daniel")
                                .email("daniel@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}