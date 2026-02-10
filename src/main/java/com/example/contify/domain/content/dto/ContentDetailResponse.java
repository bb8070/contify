package com.example.contify.domain.content.dto;

import com.example.contify.domain.content.entity.Content;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ContentDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
    private Long viewCount;

    public static ContentDetailResponse from (Content content, Long redisViewCount){
        return new ContentDetailResponse(
                content.getId(),
                content.getTitle(),
                content.getBody(),
                content.getCreatedUser().getName(),
                content.getCreatedAt(),
                redisViewCount

        ) {

        };
    }

}
