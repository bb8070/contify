package com.example.contify.domain.auth.controller;

import com.example.contify.domain.auth.dto.LoginRequest;
import com.example.contify.domain.auth.dto.LoginResponse;
import com.example.contify.global.response.ApiResponse;
import com.example.contify.global.security.JwtProvider;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final JwtProvider jwtProvider;

    public AuthController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/auth/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request){
        Long userId = request.getUserId();
        String role = request.getRole();

        String token = jwtProvider.createToken(userId, role);
        return ApiResponse.success(new LoginResponse(token));
    }

}
