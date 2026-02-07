package com.example.contify.api.content;

import com.example.contify.domain.content.dto.ContentListItem;
import com.example.contify.domain.content.dto.ContentSearchCondition;
import com.example.contify.domain.content.service.ContentBookmarkService;
import com.example.contify.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentBookmarkController {

    private final ContentBookmarkService contentBookmarkService;

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<Void> bookmark(@PathVariable Long id , @AuthenticationPrincipal User user){
        contentBookmarkService.marked(user.getId(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/unmark")
    public ResponseEntity<Void> unmark(@PathVariable Long id , @AuthenticationPrincipal User user){
        contentBookmarkService.unmarked(user.getId() , id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookmarked")
    public Page<ContentListItem> list(@ModelAttribute ContentSearchCondition condition,
                                      @PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return  contentBookmarkService.getContent(condition, pageable);
    }
}
