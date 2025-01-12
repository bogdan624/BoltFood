/**
 * Clasa principală pentru pornirea aplicației Spring Boot.
 * @author Apostu Bogdan-Alexandru
 * @version 12 Ianuarie 2025
 */
package com.bolt;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(title = "Bolt Food - API Rest Swagger Documentation")
)

/**
 * Configurare pentru schema de securitate JWT utilizată pentru autentificarea cererilor.
 * - Tipul: HTTP (Bearer Authentication)
 * - Formatul token-ului: JWT
 * - Descriere: Token JWT utilizat pentru autentificarea securizată a cererilor.
 */
@SecurityScheme(
		name = "Bearer Authentication",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT",
		description = "JWT security"
)

public class BoltSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(BoltSpringApplication.class, args);
	}
}
