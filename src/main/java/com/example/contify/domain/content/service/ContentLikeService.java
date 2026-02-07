package com.example.contify.domain.content.service;

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

    @Transactional
    public void like(Long userId, Long contentId){
        Content content = contentRepository.findById(contentId).orElseThrow(()-> new IllegalArgumentException("content not found"));
        try{
            likeRepository.save(new ContentLike(userId, content));//유니크로 중복 방지
        }catch (DataIntegrityViolationException e){
            // 이미 좋아요 한 상태 - 멱등 처리 (조용히 성공으로 봄)
            return;
        }

        contentRepository.increaseLikeCount(contentId); //원자적 증가
    }

    @Transactional
    public void unlike(Long userId, Long contentId){
        long delete = likeRepository.deleteByUserIdAndContentId(userId, contentId);
        if(delete == 0) return; //원래 좋아요를 안한 상태이면 멱등

        contentRepository.decreaseLikeCount(contentId);
    }
}
