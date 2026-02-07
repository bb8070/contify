package com.example.contify.domain.content.service;

import com.example.contify.domain.content.dto.ContentDetailResponse;
import com.example.contify.domain.content.dto.ContentListItem;
import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.repository.ContentRepository;
import com.example.contify.domain.content.repository.ContentViewRedisRepository;
import com.example.contify.global.error.ErrorCode;
import com.example.contify.global.exception.ApiException;
import jakarta.persistence.EntityManager;
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
    private final ContentViewRedisRepository viewRedisRepository;

    private final ViewLogService viewLogService;
    private final EntityManager entityManager;

    public Page<ContentListItem> getContents(ContentSearchCondition condition, Pageable pageable){
        return contentRepository.search(condition,pageable);
    }

    public Page<ContentListItem> getContentsNew(ContentSearchCondition condition, Pageable pageable){
        return contentRepository.findContents(condition, pageable);
    }

    public ContentDetailResponse getContentDetail(Long id){
        Content content = contentRepository.findDetailById(id).orElseThrow(()-> new ApiException(ErrorCode.INTERNAL_ERROR));
        Long redisViewCount = viewRedisRepository.increase(id);
        return ContentDetailResponse.from(content, redisViewCount);
    }

    public Slice<ContentListItem> getSliceContents(ContentSearchCondition condition, Pageable pageable){
        return contentRepository.searchSlice(condition,pageable);
    }

    //propagation =REQUIRED - 이미 트랜잭션이 있으면 같이 묶이고, 없으면 새로 만든다
    @Transactional
    public Content getContent(Long id){
        Content content = contentRepository.findById(id).orElseThrow(()-> new ApiException(ErrorCode.INTERNAL_ERROR));
        //Long redisViewCount = viewRedisRepository.increase(id);
        viewLogService.saveLogRequiresNew(id);
        contentRepository.flush(); // 여기서 터지면 DB 제약/DDL 문제 99%

        return content;
    }

    @Transactional
    public Content testRollback(Long id){
        Content content = contentRepository.findById(id).orElseThrow();

        content.increaseViewCount(); //기존 트랜잭션

        contentRepository.flush();
        viewLogService.saveLogRequiresNew(id); //내부 로그 트랜잭션



        throw new RuntimeException("ERROR!!!!");
    }

}
