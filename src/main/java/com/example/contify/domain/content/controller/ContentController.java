package com.example.contify.domain.content.controller;

import com.example.contify.domain.content.dto.ContentListItem;
import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.service.ContentService;
import com.example.contify.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {
    private final ContentService contentService;

    @GetMapping
    public Page<ContentListItem> list(
            @ModelAttribute ContentSearchCondition condition, //HTTP 요청 파라미터(query, form-data)를 객체(DTO)에 자동으로 매핑 - 주로 생략하지만 가독성과 의도를 보여주기 위해 넣음
            @PageableDefault(size=20 , sort="createdAt", direction = Sort.Direction.DESC)
            Pageable pageable){
        return contentService.getContents(condition, pageable);
    }
    @GetMapping("/slice")
    public Slice<ContentListItem> slice(
            @ModelAttribute ContentSearchCondition condition, //HTTP 요청 파라미터(query, form-data)를 객체(DTO)에 자동으로 매핑 - 주로 생략하지만 가독성과 의도를 보여주기 위해 넣음
            @PageableDefault(size=20 , sort="createdAt", direction = Sort.Direction.DESC)
            Pageable pageable){
        return contentService.getSliceContents(condition, pageable);
    }

    @GetMapping("/{id}")
    public Content detail(@PathVariable Long id){
        return contentService.getContent(id);
    }

    @GetMapping("/logtest/{id}")
    public Content logtest(@PathVariable Long id){
        return contentService.testRollback(id);
    }

    @GetMapping("/me")
    public ApiResponse<Long> myInfo(Authentication authentication){
        Long userId = (Long) authentication.getPrincipal();

        return ApiResponse.success(userId);
    }

}
