package com.example.contify.domain.content.service;

import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.global.error.ErrorCode;
import com.example.contify.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;

    public Page<Content> getContents(Pageable pageable){
        return contentRepository.findAll(pageable);
    }

    @Transactional
    public Content getContent(Long id){
        Content content = contentRepository.findById(id).orElseThrow(()-> new ApiException(ErrorCode.INTERNAL_ERROR));
//      DirtyChecking
        content.increaseViewCount();
        return content;
    }

}
