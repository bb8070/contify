package com.example.contify.domain.content.service;

import com.example.contify.domain.content.dto.PopularMetric;
import com.example.contify.domain.content.dto.PopularPeriod;
import com.example.contify.domain.content.redis.PopularKeyFactory;
import com.example.contify.domain.content.repository.PopularRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContentViewRankService {
    private final PopularRedisRepository popularRedisRepository;
    /*
    * 콘텐츠 조회 시 호출됨
    * - 일 /주 /월 단위로 랭킹 점소를 Redis에 누적
    * - 동시에 delta(hash)에 변경분을 기록해서
    *   나중에 배치가 DB에 반영될 수 있도록 함.
    * */
    public void increaseViewForRanking(long contentId){
        LocalDateTime now = LocalDateTime.now();
        increase(PopularPeriod.DAY, PopularMetric.VIEW, now , contentId , 1);
        increase(PopularPeriod.WEEK, PopularMetric.VIEW, now , contentId , 1);
        increase(PopularPeriod.MONTH, PopularMetric.VIEW, now, contentId , 1);
    }
    private void increase(PopularPeriod period , PopularMetric metric , LocalDateTime now , long contentId , long delta) {
        String rankingKey = PopularKeyFactory.rankingKey(period, metric, now);
        String deltaKey = PopularKeyFactory.deltaKey(period, metric, now);

        //ZSET : 실시간 인기 랭킹 점수 증가
        //HASH : 변경된 것만 DB에 반영하기 위한 증분기록
        popularRedisRepository.increase(rankingKey, deltaKey, contentId, delta);
    }

}
