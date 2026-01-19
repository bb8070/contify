package com.example.contify.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {
    @Operation(summary = "서비스 상태 체크", description = "서버 올리고 호출해봅니다~~")
    @GetMapping("/health")
    public Map<String , Object> healthCheck(){
        return Map.of("status", "UP", "time", Instant.now().toString());
    }
}
