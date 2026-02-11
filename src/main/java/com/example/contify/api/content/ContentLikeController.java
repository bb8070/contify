package com.example.contify.api.content;

import com.example.contify.domain.content.service.ContentLikeService;
import com.example.contify.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentLikeController {

    private final ContentLikeService contentLikeService;

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> like(@PathVariable Long id , @AuthenticationPrincipal Long userId){
        contentLikeService.like(userId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlike(@PathVariable Long id, @AuthenticationPrincipal Long userId){
        contentLikeService.unlike(userId, id);
        return ResponseEntity.ok().build();
    }



}
