package com.example.contify.domain.content.dto;

import com.example.contify.domain.content.entity.ContentCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentSearchCondition {
    //검색 조건을 하나의 개념을 묶음
    private String keyword;
    private ContentCategory category;
}
