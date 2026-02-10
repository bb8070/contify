package com.example.contify.domain.content.dto;

import com.example.contify.domain.content.entity.ContentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateContentRequest (
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    String title ,
    @NotBlank(message = "본문은 필수입니다.")
    String body ,
    @NotNull(message="카테고리는 필수입니다.")
    ContentCategory category
){
}
