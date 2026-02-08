package com.example.contify.domain.content.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ContentViewRedisRepository  {
    private final RedisTemplate<String, Long> redisTemplate;

    private static final String VIEW_KEY = "content:view";
    private static final String DIRTY_SET = "content:view:dirty";
    private final StringRedisTemplate stringRedisTemplate;

    /*private static final String KEY_PREFIX = "content:view";

    public Long increase(Long contentId){
        String key = KEY_PREFIX+contentId;
        return redisTemplate.opsForValue().increment(key);
    }

    public Long getCount(Long contentId){
        String key = KEY_PREFIX+contentId;
        Long value = redisTemplate.opsForValue().get(key);
        return value != null ? value : 0L;
    }
     */

    public Long increase(Long contentId){
        stringRedisTemplate.opsForSet().add(DIRTY_SET, String.valueOf(contentId));
        Long v = stringRedisTemplate.opsForValue().increment(VIEW_KEY+contentId);
        return v !=null ? v: 0L;
    }

    public Set<String> popDirtyIds(long limit){

        List<String> poped = stringRedisTemplate.opsForSet().pop(DIRTY_SET, limit);
        if(poped==null || poped.isEmpty()){
            return Collections.emptySet();
        }
        return new HashSet<>(poped);

    }

    public Long getCount(Long contentId){
        String v = stringRedisTemplate.opsForValue().get(VIEW_KEY+contentId);
        return v == null ? 0L : Long.parseLong(v);
    }

    public void clearCount(Long contentId){
        stringRedisTemplate.delete(VIEW_KEY+contentId);
    }

}
