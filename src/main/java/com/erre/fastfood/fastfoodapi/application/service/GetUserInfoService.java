package com.erre.fastfood.fastfoodapi.application.service;

import org.springframework.stereotype.Service;
import com.erre.fastfood.fastfoodapi.application.port.in.GetUserInfoQuery;
import com.erre.fastfood.fastfoodapi.domain.model.UserInfo;

@Service
public class GetUserInfoService implements GetUserInfoQuery {
    @Override
    public UserInfo handle(UserInfo current) {
        // Aquí pondremos lógica de dominio en el futuro
        return current;
    }
}
