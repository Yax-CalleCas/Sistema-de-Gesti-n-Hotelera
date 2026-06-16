package com.hotel.cibertec.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigPruebasUnitarias {

    @Bean
    public OpenAPI hotelApi() {

        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Cibertec API")
                        .version("1.0")
                        .description("Documentación automática de todos los Controllers")
                        .contact(new Contact()
                                .name("Hotel Cibertec")))
                .addSecurityItem(
                        new SecurityRequirement().addList("Bearer Authentication"))
                .schemaRequirement(
                        "Bearer Authentication",
                        new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
    }
}