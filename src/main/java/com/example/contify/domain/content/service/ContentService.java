package com.example.contify.domain.content.service;

import com.example.contify.domain.content.dto.ContentListItem;
import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.global.error.ErrorCode;
import com.example.contify.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;

    public Page<ContentListItem> getContents(ContentSearchCondition condition, Pageable pageable){
        return contentRepository.search(condition,pageable);
    }

    public Slice<ContentListItem> getSliceContents(ContentSearchCondition condition, Pageable pageable){
        return contentRepository.searchSlice(condition,pageable);
    }

    @Transactional
    public Content getContent(Long id){
        Content content = contentRepository.findById(id).orElseThrow(()-> new ApiException(ErrorCode.INTERNAL_ERROR));
//      DirtyChecking
        content.increaseViewCount();
        return content;
    }

}
