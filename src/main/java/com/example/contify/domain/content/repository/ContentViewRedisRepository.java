package com.example.contify.domain.content.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentViewRedisRepository  {
    private final RedisTemplate<String, Long> redisTemplate;
    private static final String KEY_PREFIX = "content:view";

    public Long increase(Long contentId){
        String key = KEY_PREFIX+contentId;
        return redisTemplate.opsForValue().increment(key);
    }

    public Long getCount(Long contentId){
        String key = KEY_PREFIX+contentId;
        Long value = redisTemplate.opsForValue().get(key);
        return value != null ? value : 0L;
    }
}
