package ec.com.erre.fastfood.infrastructure.commons.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
public class KeycloakProperties {

	private String issuerUri;
	private String jwkSetUri;
}
