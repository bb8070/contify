package com.example.contify.domain.content.dto;

import com.example.contify.domain.content.entity.ContentCategory;
import com.example.contify.domain.user.entity.User;

import java.time.LocalDateTime;

//실제 데이터 목록
//단순 데이터 묶음이기 때문에 record로 작성됨
//record 불변 데이터 객체를 위한 초간단 문법 - setter가 필요없음
public record ContentListItem (
    Long id,
    String title,
    ContentCategory category,
    long viewCount,
    LocalDateTime createdAt,
    String name
    ){

}
