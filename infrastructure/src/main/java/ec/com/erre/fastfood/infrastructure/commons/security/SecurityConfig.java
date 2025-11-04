package ec.com.erre.fastfood.infrastructure.commons.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

	@Value("${app.cors.allowed-origins:http://localhost:4200,https://app.erre.com}")
	private String allowedOrigins;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> authz
				// Actuator
				.requestMatchers("/actuator/**").permitAll()
				// Swagger UI (permitir todos los métodos y recursos)
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
						"/webjars/**")
				.permitAll()
				// Todo lo demás requiere autenticación
				.anyRequest().authenticated());

		http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));

		// Deshabilitar CSRF, no es necesario ya que el API es stateless
		http.csrf(csrf -> csrf.disable());

		// Configure CORS
		http.cors(cors -> {
			CorsConfigurationSource source = request -> {
				CorsConfiguration config = new CorsConfiguration();
				// Permitir múltiples orígenes desde variable de entorno
				List<String> origins = Arrays.asList(allowedOrigins.split(","));
				config.setAllowedOrigins(origins);
				config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
				config.setAllowedHeaders(Arrays.asList("*"));
				config.setAllowCredentials(true);
				config.setMaxAge(3600L); // Cache preflight por 1 hora
				return config;
			};
			cors.configurationSource(source);
		});
		return http.build();

	}

}
