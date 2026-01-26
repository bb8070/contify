package com.example.contify.global.security;

import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AccessTokenBlacklistService {
    private final StringRedisTemplate redisTemplate;
    private final JwtProvider jwtProvider;

    public AccessTokenBlacklistService(StringRedisTemplate redisTemplate, JwtProvider jwtProvider){
        this.redisTemplate = redisTemplate;
        this.jwtProvider = jwtProvider;
    }

    public String key(String accessToken){
        Claims claims = jwtProvider.parseClaims(accessToken);
        return "blacklist:jti:"+claims.getId();
    }

    public void blacklist(String accessToken , long ttlMs){
        redisTemplate.opsForValue().set(key(accessToken), "logout", Duration.ofMillis(ttlMs));
    }

    public boolean isBlackListed(String accessToken){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(accessToken)));
    }
}
