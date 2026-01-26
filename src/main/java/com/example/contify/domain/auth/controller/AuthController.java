package com.example.contify.domain.auth.controller;

import com.example.contify.domain.auth.dto.LoginRequest;
import com.example.contify.domain.auth.dto.LoginResponse;
import com.example.contify.domain.auth.dto.TokenRefreshRequest;
import com.example.contify.global.error.ErrorCode;
import com.example.contify.global.exception.ApiException;
import com.example.contify.global.response.ApiResponse;
import com.example.contify.global.security.AccessTokenBlacklistService;
import com.example.contify.global.security.JwtProvider;
import com.example.contify.global.security.RefreshTokenService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenBlacklistService blacklistService;
    private final long refreshExpMs;

    public AuthController(
            JwtProvider jwtProvider ,
            RefreshTokenService refreshTokenService ,
            AccessTokenBlacklistService blacklistService,
            @Value("${jwt.refresh-token-expiration}") long refreshExpMs
    ) {
        this.jwtProvider = jwtProvider;
        this.refreshTokenService =refreshTokenService;
        this.blacklistService = blacklistService;
        this.refreshExpMs= refreshExpMs;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request){
        Long userId = request.getUserId();
        String role = request.getRole();

        String accessToken = jwtProvider.accessToken(userId, role);
        String refreshToken = jwtProvider.refreshToken(userId, role);

        refreshTokenService.save(userId , refreshToken, refreshExpMs);

        return ApiResponse.success(new LoginResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@RequestBody TokenRefreshRequest request) throws IllegalAccessException {
        String refreshToken = request.getRefreshToken();
        Claims claims = jwtProvider.parseClaims(refreshToken);

        if(!jwtProvider.isRefreshToken(claims)){
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtProvider.getUserId(claims);
        String role = jwtProvider.getRole(claims);

        if(!refreshTokenService.matches(userId, refreshToken)){
            throw new ApiException(ErrorCode.EXPIRED_TOKEN);
        }

        String newAccess = jwtProvider.accessToken(userId, role);
        String newRefresh = jwtProvider.refreshToken(userId, role);

        //boolean rotated = refreshTokenService.rotate(userId, refreshToken, newRefresh, refreshExpMs);
        boolean rotated = refreshTokenService.rotateAtomically(userId, refreshToken, newRefresh, refreshExpMs);

        if(!rotated){
            refreshTokenService.delete(userId);
            throw new IllegalAccessException("Refresh token invalid (rotated or stolen)");
        }

        return ApiResponse.success(new LoginResponse(newAccess, newRefresh));

    }


    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorizationHeader){

        String token = authorizationHeader.replace("Bearer ","");
        Claims claims = jwtProvider.parseClaims(token);
        Long userId = jwtProvider.getUserId(claims);

        refreshTokenService.delete(userId);

        String jti= claims.getId();
        blacklistService.blacklist(jti , refreshExpMs);

        return ApiResponse.success();

    }

}
