package com.example.contify.domain.action.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ViewDeduplicator {
    private final StringRedisTemplate redis;

    /*
    * Redis 장애 시 정책은 보통 “조회수는 fail-open(그냥 카운트)”도 가능. 실습에선 일단 Redis가 정상이라고 가정하고 진행해도 OK.
    * */
    //같은 유저가 같은 글을 10분 내 재조회하면 1회로 처리
    private static final Duration TTL = Duration.ofMinutes(10);

    public boolean shouldCount(Long userId, Long contentId){
        String key = "view:"+userId+":"+contentId;

        //setIfAbsent : 원자적으로 동작 / 2명이 동시에 들어와도 한 명만 key 생성 성공
        boolean ok = redis.opsForValue().setIfAbsent(key,"1",TTL);
        return Boolean.TRUE.equals(ok); //true면 이번 조회는 카운트
    }


}
