package ec.com.erre.fastfood.infrastructure.commons.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtConverter jwtConverter;

	@Value("${app.cors.allowed-origins:http://localhost:4200,https://app.erre.cloud}")
	private String allowedOrigins;

	/**
	 * 1) Actuator chain (solo health/info/prometheus; lo demás denegado)
	 */
	@Bean
	@Order(1)
	SecurityFilterChain actuatorSecurity(HttpSecurity http) throws Exception {
		http.securityMatcher(EndpointRequest.toAnyEndpoint());

		http.csrf(csrf -> csrf.disable());
		http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(authz -> authz.requestMatchers(EndpointRequest.to("health", "info", "prometheus"))
				.permitAll().anyRequest().denyAll());

		return http.build();
	}

	/**
	 * 2) App chain (Swagger público + el resto con JWT)
	 */
	@Bean
	@Order(2)
	SecurityFilterChain appSecurity(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());

		http.cors(cors -> cors.configurationSource(request -> {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
			config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
			config.setAllowedHeaders(List.of("*"));
			config.setAllowCredentials(true);
			config.setMaxAge(3600L);
			return config;
		}));

		http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(authz -> authz.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

				// Swagger
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
						"/webjars/**")
				.permitAll()

				.anyRequest().authenticated());

		http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));

		return http.build();
	}
}
