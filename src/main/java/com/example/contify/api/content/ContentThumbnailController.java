package com.example.contify.api.content;

import com.example.contify.domain.content.service.ContentThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentThumbnailController {

    private final ContentThumbnailService contentThumbnailService;

    @PostMapping("/{contentId}/thumbnail")
    public ResponseEntity<?> upload(
            @PathVariable Long contentId ,
            @RequestParam("file")MultipartFile file,
            @AuthenticationPrincipal Long userId
    ) throws Exception{

        String url = contentThumbnailService.uploadThumbnail(userId, contentId, file);
        return ResponseEntity.ok(new ThumbnailUploadResponse(url));

    }

    record ThumbnailUploadResponse(String thumbnailUrl){}

}
