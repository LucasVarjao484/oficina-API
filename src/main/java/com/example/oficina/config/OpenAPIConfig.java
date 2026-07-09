package com.example.oficina.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "oficina API",
                version = "v1",
                contact = @Contact(
                        name = "Lucas",
                        email = "lucasvarjao@email.com",
                        url = "apioficina.com"
                )
        )
)
public class OpenAPIConfig {
}
