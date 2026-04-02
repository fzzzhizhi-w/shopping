package com.shopping.service;

import com.shopping.common.Result;
import com.shopping.dto.LoginRequest;
import com.shopping.dto.RegisterRequest;
import com.shopping.entity.User;

public interface UserService {

    Result<?> login(LoginRequest request);

    Result<?> register(RegisterRequest request);

    User getUserById(Long id);

    Result<?> updateUser(Long userId, User user);
}
