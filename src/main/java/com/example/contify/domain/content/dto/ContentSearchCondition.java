package com.example.contify.domain.content.dto;

import com.example.contify.domain.content.entity.ContentCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ContentSearchCondition {
    //검색 조건을 하나의 개념을 묶음
    private String keyword;
    private Long userId;
    private ContentCategory category;
    private List<Long> tagIds;
    private LocalDate from;
    private LocalDate to;

}
