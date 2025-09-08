package com.erre.fastfood.fastfoodapi.application.port.in;

import com.erre.fastfood.fastfoodapi.domain.model.UserInfo;

public interface GetUserInfoQuery {
    UserInfo handle(UserInfo current);
}
