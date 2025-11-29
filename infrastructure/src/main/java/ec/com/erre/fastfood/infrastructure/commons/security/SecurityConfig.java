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

	// Nuevo: leemos el context-path dinámicamente
	@Value("${server.servlet.context-path:/}")
	private String contextPath;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> authz
				// Actuator (Prometheus, health, etc.)
				.requestMatchers(contextPath + "/actuator/**").permitAll()

				// Swagger UI y documentación
				.requestMatchers(contextPath + "/swagger-ui.html", contextPath + "/swagger-ui/**",
						contextPath + "/v3/api-docs/**", contextPath + "/swagger-resources/**",
						contextPath + "/webjars/**")
				.permitAll()

				// Cualquier otro endpoint requiere autenticación
				.anyRequest().authenticated());

		http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));

		// API sin estado → deshabilitamos CSRF
		http.csrf(csrf -> csrf.disable());

		// Configuración de CORS
		http.cors(cors -> {
			CorsConfigurationSource source = request -> {
				CorsConfiguration config = new CorsConfiguration();
				List<String> origins = Arrays.asList(allowedOrigins.split(","));
				config.setAllowedOrigins(origins);
				config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
				config.setAllowedHeaders(List.of("*"));
				config.setAllowCredentials(true);
				config.setMaxAge(3600L);
				return config;
			};
			cors.configurationSource(source);
		});

		return http.build();
	}
}
