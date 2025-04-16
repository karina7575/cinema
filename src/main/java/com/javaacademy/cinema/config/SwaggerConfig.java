package com.javaacademy.cinema.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi() {
        Contact contact = new Contact()
                .email("lazukov_ao@mail.ru")
                .name("Alexander");

        Info info = new Info()
                .title("API сервис кинотеатра")
                .contact(contact)
                .description("Сервис позволяет создавать сеансы, управлять бронированиями сеансов на фильмы, "
                        + "просматривать сободные и купленные места на сеанс");

        return new OpenAPI()
                .info(info);
    }
}
