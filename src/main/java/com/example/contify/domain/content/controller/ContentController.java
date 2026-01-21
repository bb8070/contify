package com.example.contify.domain.content.controller;

import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {
    private final ContentService contentService;

    @GetMapping
    public Page<Content> list(
            ContentSearchCondition condition,
            @PageableDefault(size=20 , sort="createdAt", direction = Sort.Direction.DESC)
            Pageable pageable){
        return contentService.getContents(condition, pageable);
    }

    @GetMapping("/{id}")
    public Content detail(@PathVariable Long id){
        return contentService.getContent(id);
    }
}
