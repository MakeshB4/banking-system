package com.banking.notifications.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI notificationServiceAPI() {
        Contact contact = new Contact();
        contact.setEmail("support@banking.com");
        contact.setName("Banking Notifications Team");
        contact.setUrl("https://www.banking.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Banking Notification Service API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage notifications (Email & SMS).")
                .termsOfService("https://www.banking.com/terms")
                .license(mitLicense);

        return new OpenAPI().info(info);
    }
}