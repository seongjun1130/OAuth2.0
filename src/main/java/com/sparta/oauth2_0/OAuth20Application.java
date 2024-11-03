package com.sparta.oauth2_0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OAuth20Application {

    public static void main(String[] args) {
        SpringApplication.run(OAuth20Application.class, args);
    }

}
