package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.entity.ContentBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentBookmarkRepository extends JpaRepository<ContentBookmark,Long> , ContentBookmarkRepositoryCustom{
    boolean existsByUserIdAndContent_Id(Long userId, Long contentId);
    long deleteByUserIdAndContent_Id(Long userId, Long contentId);
    long countByContent_Id(Long contentId);
}
