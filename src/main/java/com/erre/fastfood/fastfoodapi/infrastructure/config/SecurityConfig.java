package com.erre.fastfood.fastfoodapi.infrastructure.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  @Value("${app.cors.allowed-origins:https://app.erre.com,http://localhost:3000}")
  private String allowedOriginsCsv;


  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(AbstractHttpConfigurer::disable)
      .cors(Customizer.withDefaults())
      .authorizeHttpRequests(auth -> auth
          .requestMatchers("/health", "/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
          .requestMatchers(HttpMethod.GET, "/api/hello").authenticated()
          .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    List<String> origins = Arrays.stream(allowedOriginsCsv.split(","))
                                 .map(String::trim).filter(s -> !s.isEmpty()).toList();
    cfg.setAllowedOrigins(origins);
    cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return src;
  }
}
