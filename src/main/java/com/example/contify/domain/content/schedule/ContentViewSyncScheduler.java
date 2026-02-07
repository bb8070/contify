package com.example.contify.domain.content.schedule;

import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.content.repository.ContentViewRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentViewSyncScheduler {
    private final ContentRepository contentRepository;
    private final ContentViewRedisRepository viewRedisRepository;

    @Scheduled(fixedDelay=60000)
    @Transactional
    public void syncViewCount(){
        long start = System.currentTimeMillis();

        int updated = 0;
        List<Long> contentIds = contentRepository.findAllIds();
        log.info("[VIEW SYNC] start");
        for(Long contentId : contentIds){
            Long redisCount = viewRedisRepository.getCount(contentId);
            if(redisCount>0){
                contentRepository.increaseViewCount(contentId, redisCount);
                updated++;
            }
        }
        log.info(
                "[VIEW-SYNC] end, updated={}, elapsed={}ms",
                updated,
                System.currentTimeMillis() - start
        );
    }

}
