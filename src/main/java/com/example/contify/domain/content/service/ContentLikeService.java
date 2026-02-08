package com.example.contify.domain.content.service;

import com.example.contify.domain.content.dto.PopularMetric;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.entity.ContentLike;
import com.example.contify.domain.content.repository.ContentLikeRepository;
import com.example.contify.domain.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentLikeService {
    private final ContentRepository contentRepository;
    private final ContentLikeRepository likeRepository;
    private final ContentReactionRankService contentReactionRankService;

    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public void like(Long userId, Long contentId){
        Content content = contentRepository.findById(contentId).orElseThrow(()-> new IllegalArgumentException("content not found"));
        if(likeRepository.existsByUserIdAndContentId(userId, contentId)){return;}
            likeRepository.save(new ContentLike(userId, content));
            contentRepository.increaseLikeCount(contentId); //원자적 증가
            contentReactionRankService.increase(PopularMetric.LIKE, contentId);

    }

    @Transactional
    public void unlike(Long userId, Long contentId){
        long delete = likeRepository.deleteByUserIdAndContentId(userId, contentId);
        if(delete == 0) return; //원래 좋아요를 안한 상태이면 멱등

        contentRepository.decreaseLikeCount(contentId);
        contentReactionRankService.decrease(PopularMetric.LIKE, contentId);
    }
}
