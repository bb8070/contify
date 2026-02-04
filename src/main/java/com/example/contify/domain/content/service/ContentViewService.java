package com.example.contify.domain.content.service;

import com.example.contify.domain.action.infra.ViewDeduplicator;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.repository.ContentRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentViewService {
    private final ContentRepository contentRepository;
    private final ViewDeduplicator deduplicator;

    //동시성 : 증가가 정확하게 되지 않을 경우가 있음
    @Transactional
    public void increaseViewDirtyChecking(Long userId, Long contentId){
        if(!deduplicator.shouldCount(userId, contentId)) return;
        Content content = contentRepository.findById(contentId).orElseThrow(()-> new IllegalArgumentException("content not found"));
        content.increaseViewCount();//dirty-checking

    }
    /**
     * B) DB 원자적 증가 방식 (실전에서 많이 씀)
     * - 동시성에서 안전하고 구현이 단순함
     */
    @Transactional
    public void increaseViewAtomic(Long userId, Long contentId) throws IllegalAccessException {
        if(deduplicator.shouldCount(userId, contentId)) return;

        int updated = contentRepository.increaseViewCountAtomic(contentId);
        if(updated==0) throw new IllegalAccessException("content not found");
    }

    @Transactional
    public void increaseViewOptimisticRetry(Long userId, Long contentId) {
        int maxRetries = 10;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                Content content = contentRepository.findById(contentId)
                        .orElseThrow();
                content.increaseViewCount();
                contentRepository.save(content);
                return; // 성공!

            } catch (OptimisticLockException | StaleObjectStateException e) {
                if (attempt == maxRetries - 1) {
                    throw e; // 마지막에도 실패하면 에러
                }
                // 재시도 전 대기 (랜덤으로 충돌 회피)
                try {
                    Thread.sleep((long) (Math.random() * 50));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    protected void doIncreaseWithOptimisticLock(Long contentId){
        Content content = contentRepository.findById(contentId)
                .orElseThrow(()-> new IllegalArgumentException("content not found"));
        content.increaseViewCount();//version 충돌시 commit 시점에 예외
    }


}
