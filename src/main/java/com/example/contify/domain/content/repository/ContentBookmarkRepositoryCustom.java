package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.dto.ContentListItem;
import com.example.contify.domain.content.dto.ContentSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentBookmarkRepositoryCustom {
    Page<ContentListItem> findBookMarkContentsByUserId(ContentSearchCondition condition, Pageable pageable);
}
