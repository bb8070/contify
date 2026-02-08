package com.example.contify.domain.content.service;

import com.example.contify.domain.content.dto.PopularMetric;
import com.example.contify.domain.content.dto.PopularPeriod;
import com.example.contify.domain.content.redis.PopularKeyFactory;
import com.example.contify.domain.content.repository.PopularRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ContentReactionRankService {

    private final PopularRedisRepository popularRedisRepository;
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    public void increase(PopularMetric metric , long contentId){
        apply(metric, contentId, 1);
    }

    public void decrease(PopularMetric metric , long contentId){
        apply(metric, contentId, -1);
    }

    private void apply(PopularMetric metric, long contentId, long delta){
        LocalDateTime now = LocalDateTime.now(ZONE);

        applyOne(PopularPeriod.DAY, metric, now, contentId, delta);
        applyOne(PopularPeriod.WEEK, metric, now, contentId, delta);
        applyOne(PopularPeriod.MONTH, metric, now, contentId, delta);

    }

    private void applyOne(PopularPeriod period , PopularMetric metric , LocalDateTime now, long contentId , long delta){
        String rankingKey = PopularKeyFactory.rankingKey(period, metric, now);
        String deltaKey = PopularKeyFactory.deltaKey(period, metric, now);
        popularRedisRepository.increase(rankingKey, deltaKey, contentId, delta);

    }


}
