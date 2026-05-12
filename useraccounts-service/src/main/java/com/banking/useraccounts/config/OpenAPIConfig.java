package com.banking.useraccounts.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI userAccountsServiceAPI() {
        Contact contact = new Contact();
        contact.setEmail("support@banking.com");
        contact.setName("Banking UserAccounts Team");
        contact.setUrl("https://www.banking.com");

        License bankingLicense = new License()
                .name("Banking License")
                .url("https://bankinglicense.com/licenses/mit/");

        Info info = new Info()
                .title("Banking UserAccounts Service API")
                .version("1.0")
                .contact(contact)
                .description("API for User and Accounts Creation")
                .termsOfService("https://www.banking.com/terms")
                .license(bankingLicense);

        return new OpenAPI().info(info);
    }
}