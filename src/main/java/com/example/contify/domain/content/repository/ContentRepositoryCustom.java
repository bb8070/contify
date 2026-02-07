package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.dto.ContentListItem;
import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ContentRepositoryCustom {
    //QueryDSL 검색. CRUD와 같은 계층에 섞지 않음. 검색 로직은 복잡해질수있다.
    Page<ContentListItem> search (ContentSearchCondition condition , Pageable pageable);
    Slice<ContentListItem> searchSlice (ContentSearchCondition condition , Pageable pageable);
    Page<ContentListItem> findContents(ContentSearchCondition condition,Pageable pageable);
    Optional<Content> findDetailById(Long Id);


}
