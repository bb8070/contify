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

    public static ContentDetailResponse from (Content content){
        return new ContentDetailResponse(
                content.getId(),
                content.getTitle(),
                content.getBody(),
                content.getCreatedBy().getName(),
                content.getCreatedAt()
        ) {

        };
    }

}
