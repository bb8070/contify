package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepositoryCustom {
    Page<Content> search (ContentSearchCondition condition , Pageable pageable);
}
