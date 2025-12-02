package ec.com.erre.fastfood.infrastructure.commons.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtConverter jwtConverter;

	@Value("${app.cors.allowed-origins:http://localhost:4200,https://app.erre.cloud}")
	private String allowedOrigins;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

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

		http.authorizeHttpRequests(authz -> authz
				// Preflight
				.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

				// Actuator: SOLO lo necesario (esto permite /fastfood/api/actuator/... automáticamente)
				.requestMatchers("/actuator/health/**").permitAll().requestMatchers("/actuator/prometheus").permitAll()
				.requestMatchers("/actuator/info").permitAll()

				// Swagger / OpenAPI
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
						"/webjars/**")
				.permitAll()

				// Todo lo demás → JWT
				.anyRequest().authenticated());

		http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));

		return http.build();
	}
}
