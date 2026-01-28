package com.example.contify.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    //엔터티 생성 시점 > DB 저장 시점이 아님
    //테스크 / 배치 / 재저장시 시간 꼬일 수 있음
    //update 할때 createdAt이 실수로 바뀔 위험이 있음

    //JPA가 주입 시점에 자동 주입
    //DB관점에서 언제 생성되고 수정되었는지 보장함
    //실무 표준
    @CreatedDate
    @Column(nullable = false, updatable = false, name="created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name="updated_at")
    private LocalDateTime updatedAt;

}
