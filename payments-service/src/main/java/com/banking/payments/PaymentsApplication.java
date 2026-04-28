package com.banking.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class PaymentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentsApplication.class, args);
    }

}