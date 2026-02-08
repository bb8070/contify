package com.example.contify.domain.content.schedule;

import com.example.contify.domain.content.dto.PopularMetric;
import com.example.contify.domain.content.dto.PopularPeriod;
import com.example.contify.domain.content.redis.PopularKeyFactory;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.content.repository.PopularRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularDeltaSyncScheduler {

    private final PopularRedisRepository popularRedisRepository;
    private final ContentRepository contentRepository;

    /*
    * 1분마다 Redis에 쌓인 delta(view 변경분)을 읽어서
    * DB view_count에 반영
    *
    * - 전체 콘텐츠를 스캔하지않고, 변경된 콘텐츠만 DB 업데이트
    *
    * */
    @Scheduled(fixedDelay=6000)
    @Transactional
    public void syncWeeklyViewDelta(){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        syncOne(now, PopularPeriod.WEEK, PopularMetric.VIEW);
        syncOne(now, PopularPeriod.WEEK, PopularMetric.LIKE);
        syncOne(now, PopularPeriod.MONTH, PopularMetric.BOOKMARK);

    }
    private void syncOne(LocalDateTime now , PopularPeriod period, PopularMetric metric){
        String deltaKey = PopularKeyFactory.deltaKey(period, metric, now);
        Map<Long, Long> deltas = popularRedisRepository.readAllDeltas(deltaKey);
        if(deltas.isEmpty()) return;

        for(var e : deltas.entrySet()){
            Long contentId = e.getKey();
            Long delta = e.getValue();

            try{
                switch(metric){
                    case VIEW -> contentRepository.increaseViewCount(contentId, delta);
                    case LIKE -> contentRepository.increaseLikeCount(contentId,delta);
                    case BOOKMARK -> contentRepository.increaseBookmarkCount(contentId, delta);
                }
                popularRedisRepository.clearDelta(deltaKey, contentId);
            }catch(Exception exception){

            }

        }


    }



}
