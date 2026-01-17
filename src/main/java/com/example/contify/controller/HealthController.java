package com.example.contify.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String , Object> healthCheck(){
        return Map.of("status", "UP", "time", Instant.now().toString());
    }
}
