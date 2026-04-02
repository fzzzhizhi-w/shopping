package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.LoginRequest;
import com.shopping.dto.RegisterRequest;
import com.shopping.entity.User;
import com.shopping.interceptor.UserContext;
import com.shopping.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @GetMapping("/info")
    public Result<?> info() {
        Long userId = UserContext.get();
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error(404, "User not found");
        }
        user.setPassword(null);
        return Result.success(user);
    }
}
