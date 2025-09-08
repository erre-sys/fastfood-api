package com.erre.fastfood.fastfoodapi.domain.model;

import java.util.List;

public record UserInfo(String sub, String username, List<String> roles) {}
