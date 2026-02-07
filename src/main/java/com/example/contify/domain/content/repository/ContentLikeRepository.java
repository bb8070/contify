package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.entity.ContentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentLikeRepository extends JpaRepository<ContentLike, Long> {
    boolean existsByUserIdAndContentId (Long userId, Long contentId);
    long countByContentId(Long contentId);
    long deleteByUserIdAndContentId(Long userId , Long contentId);
}
