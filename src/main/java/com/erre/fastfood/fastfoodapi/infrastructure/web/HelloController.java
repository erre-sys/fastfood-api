package com.erre.fastfood.fastfoodapi.infrastructure.web;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erre.fastfood.fastfoodapi.application.port.in.GetUserInfoQuery;
import com.erre.fastfood.fastfoodapi.domain.model.UserInfo;

@RestController
public class HelloController {

  private final GetUserInfoQuery useCase;

  public HelloController(GetUserInfoQuery useCase) {
    this.useCase = useCase;
  }

  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "ok");
  }

  @GetMapping("/api/hello")
  public UserInfo hello(@AuthenticationPrincipal Jwt jwt) {
    UserInfo ui = new UserInfo(
            jwt.getSubject(),
            jwt.getClaimAsString("preferred_username"),
            extractRealmRoles(jwt)
    );
    return useCase.handle(ui);
  }

  @SuppressWarnings("unchecked")
  private static List<String> extractRealmRoles(Jwt jwt) {
    Object realmAccessObj = jwt.getClaim("realm_access");
    if (!(realmAccessObj instanceof Map<?, ?> realmAccess)) return List.of();
    Object rolesObj = realmAccess.get("roles");
    if (!(rolesObj instanceof List<?> rawList)) return List.of();
    return rawList.stream().filter(String.class::isInstance).map(String.class::cast).toList();
  }
}
