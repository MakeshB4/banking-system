package com.banking.useraccounts.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI paymentsServiceAPI() {
        Contact contact = new Contact();
        contact.setEmail("support@banking.com");
        contact.setName("Banking Payments Team");
        contact.setUrl("https://www.banking.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Banking Payments Service API")
                .version("1.0")
                .contact(contact)
                .description("API for Payments Creation")
                .termsOfService("https://www.banking.com/terms")
                .license(mitLicense);

        return new OpenAPI().info(info);
    }
}