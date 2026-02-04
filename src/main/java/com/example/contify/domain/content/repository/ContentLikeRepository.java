package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.entity.ContentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentLikeRepository extends JpaRepository<ContentLike, Long> {
    boolean existsByUserIdAndContent_Id (Long userId, Long contentId);
    long countByContent_Id(Long contentId);
    long deleteByUserIdAndContent_Id(Long userId , Long contentId);
}
