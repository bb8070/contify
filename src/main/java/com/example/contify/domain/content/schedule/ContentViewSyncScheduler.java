package com.example.contify.domain.content.schedule;

import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.content.repository.ContentViewRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentViewSyncScheduler {
    private final ContentRepository contentRepository;
    private final ContentViewRedisRepository viewRedisRepository;
    private final View view;

    @Scheduled(fixedDelay=60000)
    @Transactional
    public void syncViewCount(){

        /*
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
        */
        long start = System.currentTimeMillis();
        int updated = 0;
        log.info("[VIEW SYNC] start");

        Set<String> dirtyIds = viewRedisRepository.popDirtyIds(500);
        if(dirtyIds.isEmpty()) return;
        for(String idStr : dirtyIds){
            long contentId = Long.parseLong(idStr);
            long redisCount = viewRedisRepository.getCount(contentId);
            if(redisCount>0){
                contentRepository.increaseViewCount(contentId, redisCount);
                viewRedisRepository.clearCount(contentId);
            }

        }

        log.info(
                "[VIEW-SYNC] end, updatedContents={}, elapsed={}ms",
                dirtyIds.toString(),
                System.currentTimeMillis() - start
        );

    }

}
